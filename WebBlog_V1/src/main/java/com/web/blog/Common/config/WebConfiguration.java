package com.web.blog.Common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://lawliet0521.com/api")
                .allowedOrigins("http://localhost:8081")
                .allowedOrigins("http://localhost:8080")
                .allowedOrigins("http://lawliet0521.com")
                .allowedOrigins("http://lawliet0521.com/blog")
                .allowedOrigins("localhost/:1");
    }
}
