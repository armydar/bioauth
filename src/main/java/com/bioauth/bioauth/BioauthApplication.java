package com.bioauth.bioauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BioauthApplication {

    public static void main(String[] args) {        
        SpringApplicationBuilder builder = new SpringApplicationBuilder(BioauthApplication.class);

        builder.headless(false);

        ConfigurableApplicationContext context = builder.run(args);
    }

}
