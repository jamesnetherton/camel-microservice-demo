package com.github.jamesnetherton.client;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.btm.ZipkinFraction;

public class Main {
    public static void main(String args[]) throws Exception {
        Swarm container = new Swarm();

        if (zipkinAvailable()) {
            String host = System.getenv("ZIPKIN_SERVICE_HOST");
            String port = System.getenv("ZIPKIN_SERVICE_PORT_ZIPKIN_WEB");
            String zipkinUrl = String.format("http://%s:%s/api/v1/spans", host, port);

            container.fraction(
                new ZipkinFraction("name-service")
                    .reportAsync(zipkinUrl)
                    .sampleRate(1.0f)
            );
        }

        container.start();
        container.deploy();
    }

    private static boolean zipkinAvailable() {
        return System.getenv("ZIPKIN_SERVICE_HOST") != null &&
               System.getenv("ZIPKIN_SERVICE_PORT_ZIPKIN_WEB") != null;
    }
}
