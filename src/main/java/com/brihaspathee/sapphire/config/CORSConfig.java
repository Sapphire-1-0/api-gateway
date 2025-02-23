package com.brihaspathee.sapphire.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 19, February 2025
 * Time: 12:06 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.config
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
//@Configuration
public class CORSConfig { //implements WebFluxConfigurer {


//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        log.info("CORS Configured");
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000") // Your frontend URL
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
}
