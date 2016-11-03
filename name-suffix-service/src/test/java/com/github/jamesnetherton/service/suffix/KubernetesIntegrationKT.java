package com.github.jamesnetherton.service.suffix;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.fabric8.kubernetes.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
@RunAsClient
public class KubernetesIntegrationKT {

    @ArquillianResource
    KubernetesClient client;

    @Test
    public void testAppProvisionsRunningPods() throws Exception {
        assertThat(client).deployments().pods().isPodReadyForPeriod();
    }
}
