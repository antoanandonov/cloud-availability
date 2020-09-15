package com.andonov.cloud.availability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CloudWatcherStarter extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CloudWatcherStarter.class, args);
        System.out.println("Spring boot Cloud-Watcher-Starter started successfully!");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CloudWatcherStarter.class);
    }

}
