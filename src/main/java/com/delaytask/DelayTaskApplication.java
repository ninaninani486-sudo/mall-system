package com.delaytask;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.delaytask.mapper")
@EnableScheduling
public class DelayTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(DelayTaskApplication.class, args);
    }
}
