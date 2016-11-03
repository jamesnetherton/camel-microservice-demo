package com.github.jamesnetherton.client.producer;

import com.github.jamesnetherton.client.interceptor.ClientRequestInterceptor;
import com.github.jamesnetherton.client.interceptor.ClientResponseInterceptor;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ResteasyClientProducer {

    @Produces
    public ResteasyClient resteasyClient() {
        ResteasyClient client = (ResteasyClient) ResteasyClientBuilder.newClient();
        client.register(ClientRequestInterceptor.class);
        client.register(ClientResponseInterceptor.class);
        return client;
    }
}
