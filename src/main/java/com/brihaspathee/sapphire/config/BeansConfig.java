package com.brihaspathee.sapphire.config;

import com.brihaspathee.sapphire.filter.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, February 2025
 * Time: 1:34 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.config
 * To change this template use File | Settings | File and Code Template
 */
@Configuration
public class BeansConfig {

    /**
     * Provides a configured WebClient bean for making HTTP requests.
     *
     * @return a new WebClient instance built using the WebClient builder.
     */
    @Bean
    public WebClient getWebClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:7095/api/v1/auth")
                .build();
    }
    
}
