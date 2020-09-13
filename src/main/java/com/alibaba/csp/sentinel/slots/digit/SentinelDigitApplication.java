package com.alibaba.csp.sentinel.slots.digit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SentinelDigitApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelDigitApplication.class, args);
    }

}
