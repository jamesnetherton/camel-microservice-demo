package com.github.jamesnetherton.service.suffix;

import com.github.kristofa.brave.scribe.ScribeSpanCollector;
import org.apache.camel.CamelContext;
import org.apache.camel.zipkin.ZipkinTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = {"ZIPKIN_SERVICE_HOST", "ZIPKIN_SERVICE_PORT_ZIPKIN_DATA"})
public class ZipkinConfig {

    @Bean
    public ScribeSpanCollector spanCollector(@Value("#{environment['ZIPKIN_SERVICE_HOST']}") String host, @Value("#{environment['ZIPKIN_SERVICE_PORT_ZIPKIN_DATA']}") int port) {
        return new ScribeSpanCollector(host, port);
    }

    @Bean
    public ZipkinTracer zipkinTracer(ScribeSpanCollector scribeSpanCollector, CamelContext camelContext) {
        ZipkinTracer tracer = new ZipkinTracer();
        tracer.setServiceName("name-suffix-service");
        tracer.addServerServiceMapping("name-suffix-service", "name-suffix-service");
        tracer.setSpanCollector(scribeSpanCollector);
        tracer.setIncludeMessageBodyStreams(true);
        tracer.init(camelContext);
        return tracer;
    }
}
