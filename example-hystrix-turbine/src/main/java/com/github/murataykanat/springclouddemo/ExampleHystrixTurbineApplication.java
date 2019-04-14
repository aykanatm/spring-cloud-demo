package com.github.murataykanat.springclouddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

@SpringBootApplication
@EnableTurbineStream
public class ExampleHystrixTurbineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleHystrixTurbineApplication.class, args);
    }

}
