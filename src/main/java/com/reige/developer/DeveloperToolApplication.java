package com.reige.developer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.reige.developer.*"})
@MapperScan("com.reige.developer.module.*.mapper")
public class DeveloperToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeveloperToolApplication.class, args);
    }

}
