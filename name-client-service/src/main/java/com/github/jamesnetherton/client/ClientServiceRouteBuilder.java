package com.github.jamesnetherton.client;

import com.github.jamesnetherton.lolcat4j.Lol;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ContextName("client-camel-context")
public class ClientServiceRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:generateName?period=10000").id("name-client-service")
        .hystrix()
           .bean("nameGenerator")
        .onFallback()
            .setBody(constant("Sorry. Name could not be generated."))
        .end()
        .process(exchange -> {
            String name = exchange.getIn().getBody(String.class);
            Lol lol = Lol.builder()
                .text(name)
                .build();
            lol.cat();
        });
    }
}
