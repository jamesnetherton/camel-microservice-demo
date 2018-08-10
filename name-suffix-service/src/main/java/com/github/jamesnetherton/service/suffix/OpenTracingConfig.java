package com.github.jamesnetherton.service.suffix;

import io.jaegertracing.samplers.ConstSampler;
import org.apache.camel.CamelContext;
import org.apache.camel.opentracing.OpenTracingTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = {"OPENTRACING_SERVICE_HOST", "OPENTRACING_SERVICE_PORT_OPENTRACING_DATA"})
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
        return new io.jaegertracing.Configuration("name-suffix-service")
            .withSampler(samplerConfig)
            .withReporter(reporterConfig);
    }

    @Bean
    public OpenTracingTracer openTracingTracer(io.jaegertracing.Configuration jaegerConfiguration, CamelContext camelContext) {
        OpenTracingTracer tracer = new OpenTracingTracer();
        tracer.setTracer(jaegerConfiguration.getTracer());
        tracer.init(camelContext);
        return tracer;
    }
}
