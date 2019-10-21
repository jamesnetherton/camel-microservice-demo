package com.github.jamesnetherton.service.prefix;

import io.jaegertracing.samplers.ConstSampler;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.Route;
import org.apache.camel.opentracing.OpenTracingTracer;
import org.apache.camel.spi.RoutePolicy;
import org.apache.camel.spi.RoutePolicyFactory;
import org.apache.camel.support.RoutePolicySupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Configuration
public class OpenTracingConfig {

    @Bean
    public io.jaegertracing.Configuration jaegerConfiguration(@Value("#{environment['OPENTRACING_SERVICE_HOST'] ?: 'localhost'}") String host,
                                                              @Value("#{environment['OPENTRACING_SERVICE_PORT_OPENTRACING_DATA'] ?: 6831}") int port) {
        io.jaegertracing.Configuration.SamplerConfiguration samplerConfig = new io.jaegertracing.Configuration.SamplerConfiguration()
            .withType(ConstSampler.TYPE)
            .withParam(1);
        io.jaegertracing.Configuration.SenderConfiguration senderConfig = new io.jaegertracing.Configuration.SenderConfiguration()
            .withAgentHost(host)
            .withAgentPort(port);
        io.jaegertracing.Configuration.ReporterConfiguration reporterConfig = new io.jaegertracing.Configuration.ReporterConfiguration()
            .withLogSpans(true)
            .withFlushInterval(1000)
            .withMaxQueueSize(10000)
            .withSender(senderConfig);
        return new io.jaegertracing.Configuration("name-prefix-service")
            .withSampler(samplerConfig)
            .withReporter(reporterConfig);
    }

    @Bean
    public OpenTracingTracer openTracingTracer(io.jaegertracing.Configuration jaegerConfiguration, CamelContext camelContext) {
        // This is a hack to manually decode the uber-trace-id header value because camel forces use of TEXT_MAP format
        List<RoutePolicyFactory> routePolicyFactories = camelContext.getRoutePolicyFactories();
        routePolicyFactories.add(0, new TraceIdDecodingRoutePolicyFactory());

        OpenTracingTracer tracer = new OpenTracingTracer();
        tracer.setTracer(jaegerConfiguration.getTracer());
        tracer.init(camelContext);
        return tracer;
    }

    static class TraceIdDecodingRoutePolicyFactory implements RoutePolicyFactory {
        @Override
        public RoutePolicy createRoutePolicy(CamelContext camelContext, String routeId, NamedNode route) {
            return new TraceIdDecodingRoutePolicy();
        }
    }

    static class TraceIdDecodingRoutePolicy extends RoutePolicySupport {

        private static final String HEADER_UBER_TRACE_ID = "uber-trace-id";

        @Override
        public void onExchangeBegin(Route route, Exchange exchange) {
            String traceId = exchange.getIn().getHeader(HEADER_UBER_TRACE_ID, String.class);
            if (traceId != null) {
                try {
                    exchange.getIn().setHeader(HEADER_UBER_TRACE_ID, URLDecoder.decode(traceId, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
