package com.github.jamesnetherton.service.suffix.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LastNameProcessor implements Processor {
    private List<String> lastNames = new ArrayList<>();
    private List<String> words = new ArrayList<>();
    private Random random = new Random();

    public LastNameProcessor() {
        buildList(lastNames, "lastnames.txt");
        buildList(words, "words.txt");
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String name = exchange.getIn().getBody(String.class);
        String randomLastName = lastNames.get(random.nextInt(lastNames.size() - 1));
        String randomWord = words.get(random.nextInt(words.size() - 1));

        exchange.getIn().setBody(name + " " + randomLastName + "-" + randomWord);
    }

    private void buildList(List<String> list, String resource) {
        InputStream inputStream = LastNameProcessor.class.getClassLoader().getResourceAsStream(resource);

        if (inputStream == null) {
            throw new IllegalStateException("InputStream is null");
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading from InputStream", e);
        }
    }
}
