package com.team3.collections.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan("com.team3.collections")
@PropertySource("classpath:application.properties")
public class SpringConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
