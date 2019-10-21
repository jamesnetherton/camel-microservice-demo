package com.github.jamesnetherton.service.prefix;

import com.github.jamesnetherton.service.prefix.processor.TitleProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PrefixServiceRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .contextPath("/api")
            .component("servlet");

        rest()
            .post("/name/prefix")
                .route().routeId("name-prefix-service")
                    .removeHeaders("CamelHttp*")
                    .process(new TitleProcessor())
                    .to("http://{{suffix.service.host.name}}/api/name/suffix")
                .end();
    }
}
