package com.github.jamesnetherton.client.generator;

import org.apache.camel.PropertyInject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static javax.ws.rs.core.Response.Status.OK;

@ApplicationScoped
@Named("nameGenerator")
public class NameGenerator {
    private List<String> names = new ArrayList<>();
    private Random random = new Random();

    @Inject
    private Client client;

    @PropertyInject(value = "NAME_PREFIX_SERVICE_SERVICE_HOST", defaultValue = "localhost:8081")
    private String prefixServiceHost;

    public NameGenerator() {
        InputStream resource = NameGenerator.class.getClassLoader().getResourceAsStream("/names.txt");
        if (resource == null) {
            throw new IllegalStateException("Unable to load names");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading names", e);
        }
    }

    public String generateName() {
        String result = "";
        String randomName = names.get(random.nextInt(names.size() - 1));

        Form form = new Form();
        form.param("name", randomName);

        Entity<Form> entity = Entity.form(form);

        Response response = client.target("http://" + prefixServiceHost + "/api/name/prefix")
            .request()
            .post(entity);

        if (response != null) {
            int statusCode = response.getStatus();
            if (statusCode != OK.getStatusCode()) {
                response.close();
                throw new IllegalStateException("Name prefix service returned status code" + statusCode);
            }

            result = response.readEntity(String.class);
            response.close();
        } else {
            throw new IllegalStateException("Unable to retrieve client response");
        }

        return result;
    }
}
