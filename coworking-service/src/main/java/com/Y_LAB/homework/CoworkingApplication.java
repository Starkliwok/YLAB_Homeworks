package com.Y_LAB.homework;

import com.Y_LAB.homework.aop.annotation.EnableAudit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAudit
public class CoworkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoworkingApplication.class, args);
    }
}