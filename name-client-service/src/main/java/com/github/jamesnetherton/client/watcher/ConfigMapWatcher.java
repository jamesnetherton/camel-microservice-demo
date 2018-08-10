package com.github.jamesnetherton.client.watcher;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.component.timer.TimerConsumer;
import org.apache.camel.management.event.CamelContextStartingEvent;
import org.apache.camel.management.event.CamelContextStoppingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ConfigMapWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigMapWatcher.class);

    @Inject
    private KubernetesClient client;

    public void onContextStarting(@Observes CamelContextStartingEvent event) {

        /**
         *  This is a bit of a hack to adjust the timer endpoint period parameter on-the-fly from a ConfigMap update.
         *
         *  Alternatives could have been to use:
         *      - JMX
         *      - For Spring applications - Spring Cloud Kubernetes provides OOTB support for application reloads from ConfigMap changes
         *
         *      https://github.com/fabric8io/spring-cloud-kubernetes
         */
        if (isRunningInKubernetes()) {
            CamelContext context = event.getContext();

            client.configMaps().watch(new Watcher<ConfigMap>() {
                @Override
                public void eventReceived(Action action, ConfigMap configMap) {
                    if (action.equals(Action.MODIFIED) && configMap.getMetadata().getName().equals("client-service-config")) {
                        LOG.info("Reloading config map");

                        String timerPeriod = configMap.getData().get("timer.period");
                        Route route = context.getRoute("name-client-service");
                        if (route == null) {
                            LOG.error("Failed updating timer.period. Route name-client-service not found.");
                            return;
                        }

                        TimerConsumer consumer = (TimerConsumer) route.getConsumer();
                        LOG.info("Adjusting timer period to {}ms", timerPeriod);
                        consumer.getEndpoint().setPeriod(Long.parseLong(timerPeriod));

                        LOG.info("Restarting consumer");
                        try {
                            consumer.stop();
                            consumer.start();
                        } catch (Exception e) {
                            LOG.error("Error restarting consumer {}", e);
                        }
                    }
                }

                @Override
                public void onClose(KubernetesClientException e) {
                }
            });
        }
    }

    public void onContextStopping(@Observes CamelContextStoppingEvent event) {
        client.close();
    }

    private boolean isRunningInKubernetes() {
        return System.getenv("KUBERNETES_SERVICE_HOST") != null;
    }
}
