package com.brihaspathee.sapphire.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 5/9/25
 * Time: 3:22 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.config
 * To change this template use File | Settings | File and Code Template
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "application.auth-service")
public class AuthServiceConfig {

    /**
     * Represents the host configuration for the authentication service.
     * This value is loaded from the application's configuration properties
     * with the prefix `application.auth-service`.
     */
    private String host;

    /**
     * Represents the port configuration for the authentication service.
     * This value is loaded from the application's configuration properties
     * with the prefix `application.auth-service`.
     */
    private String port;
}
