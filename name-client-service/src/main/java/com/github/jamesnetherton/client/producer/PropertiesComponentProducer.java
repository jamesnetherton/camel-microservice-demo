package com.github.jamesnetherton.client.producer;

import org.apache.camel.component.properties.PropertiesComponent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.util.Map;
import java.util.Properties;

@ApplicationScoped
public class PropertiesComponentProducer {

    @Produces
    @Named("properties")
    public PropertiesComponent propertiesComponent() {
        PropertiesComponent component = new PropertiesComponent();
        Properties properties = new Properties();

        for (Map.Entry<String, String> env : System.getenv().entrySet()) {
            properties.put(env.getKey(), env.getValue());
        }

        component.setInitialProperties(properties);
        return component;
    }
}
