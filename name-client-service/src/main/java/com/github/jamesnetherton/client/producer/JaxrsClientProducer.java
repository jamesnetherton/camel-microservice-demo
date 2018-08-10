package com.github.jamesnetherton.client.producer;

import io.opentracing.contrib.jaxrs2.client.ClientTracingFeature;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@ApplicationScoped
public class JaxrsClientProducer {

    @Produces
    public Client resteasyClient() {
        return ClientBuilder.newBuilder()
            .register(ClientTracingFeature.class)
            .build();
    }
}
