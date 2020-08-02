package com.team3.collections.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.team3.collections")
@PropertySource("classpath:application.properties")
public class SpringConfig {
}
