package com.software.magneto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class})
public class MagnetoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagnetoApplication.class, args);
    }

}
