package com.histsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
//        SpringApplication app = new SpringApplication(App.class);
//        app.setDefaultProperties(Collections .singletonMap("server.port", "8083"));
//        app.run(args);
    }
}
