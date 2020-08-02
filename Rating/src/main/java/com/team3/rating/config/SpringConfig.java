package com.team3.rating.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.team3.rating")
@PropertySource("classpath:application.properties")
public class SpringConfig {
}
