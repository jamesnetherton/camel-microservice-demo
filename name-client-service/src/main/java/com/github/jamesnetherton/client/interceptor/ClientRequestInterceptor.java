package com.github.jamesnetherton.client.interceptor;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.jaxrs2.BraveClientRequestFilter;
import org.wildfly.swarm.jaxrs.btm.BraveLookup;

import javax.naming.NamingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ClientRequestInterceptor implements ClientRequestFilter {

    private final BraveClientRequestFilter delegate;
    private final Brave brave;

    public ClientRequestInterceptor() {
        try {
            this.brave = BraveLookup.lookup().get();
            this.delegate = new BraveClientRequestFilter(
                new DefaultSpanNameProvider(),
                brave.clientRequestInterceptor()
            );
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup brave", e);
        }
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        delegate.filter(clientRequestContext);
    }
}
