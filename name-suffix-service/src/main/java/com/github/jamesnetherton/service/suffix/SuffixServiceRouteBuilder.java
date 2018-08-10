package com.github.jamesnetherton.service.suffix;

import com.github.jamesnetherton.service.suffix.processor.LastNameProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SuffixServiceRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .component("servlet")
            .contextPath("/api");

        rest()
            .post("/name/suffix")
            .route().id("name-suffix-service")
                .process(new LastNameProcessor());
    }
}
