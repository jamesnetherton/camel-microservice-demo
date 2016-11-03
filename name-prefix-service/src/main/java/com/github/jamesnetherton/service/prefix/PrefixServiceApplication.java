package com.github.jamesnetherton.service.prefix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.github.jamesnetherton.service.prefix.servlet")
public class PrefixServiceApplication {

    public static void main(String args[]) {
        SpringApplication.run(PrefixServiceApplication.class, args);
    }
}
