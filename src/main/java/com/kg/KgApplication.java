package com.kg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kg.infrastructure.mapper")
public class KgApplication {
    public static void main(String[] args) {
        SpringApplication.run(KgApplication.class, args);
    }
}
