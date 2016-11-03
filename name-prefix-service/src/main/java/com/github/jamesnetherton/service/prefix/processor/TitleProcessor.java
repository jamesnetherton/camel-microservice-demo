package com.github.jamesnetherton.service.prefix.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TitleProcessor implements Processor {
    private List<String> titles = new ArrayList<>();
    private Random random = new Random();

    public TitleProcessor() {
        InputStream resource = TitleProcessor.class.getClassLoader().getResourceAsStream("titles.txt");
        if (resource == null) {
            throw new IllegalStateException("Unable to load titles");
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            String line;
            while((line = reader.readLine()) != null) {
                titles.add(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading titles", e);
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String name = exchange.getIn().getHeader("name", String.class);
        String randomTitle = titles.get(random.nextInt(titles.size() - 1));

        exchange.getOut().setBody(randomTitle + " " + name);
    }
}
