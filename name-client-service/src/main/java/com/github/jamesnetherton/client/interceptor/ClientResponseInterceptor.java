package com.github.jamesnetherton.client.interceptor;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.jaxrs2.BraveClientResponseFilter;
import org.wildfly.swarm.jaxrs.btm.BraveLookup;

import javax.naming.NamingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ClientResponseInterceptor implements ClientResponseFilter {

    private final BraveClientResponseFilter delegate;
    private final Brave brave;

    public ClientResponseInterceptor() {
        try {
            this.brave = BraveLookup.lookup().get();
            this.delegate = new BraveClientResponseFilter(
                brave.clientResponseInterceptor()
            );
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup brave", e);
        }
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) throws IOException {
        this.delegate.filter(clientRequestContext, clientResponseContext);
    }
}
