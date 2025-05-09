package com.brihaspathee.sapphire.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 5/9/25
 * Time: 3:23 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.config
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
public class StartupLogger {

    /**
     * This method is triggered when the application is fully initialized and ready to service requests.
     * It listens for the {@link ApplicationReadyEvent}
     * to confirm successful startup and logs a message indicating the application is operational.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startUpComplete(){
        log.info("✅ API Gateway is UP and ready!");
    }
}
