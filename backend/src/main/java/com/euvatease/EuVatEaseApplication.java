package com.euvatease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class EuVatEaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuVatEaseApplication.class, args);
    }
}
