package com.roadmapsh.urlshortener.config;

import com.roadmapsh.urlshortener.utils.ShortCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ShortCodeGenerator shortCodeGenerator() {
        return new ShortCodeGenerator();
    }
}
