package com.brihaspathee.sapphire.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, February 2025
 * Time: 3:56 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.scheduler
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SapphireRouteRefresher {

    /**
     * The eventPublisher is an instance of ApplicationEventPublisher
     * that is used to publish application-specific events. It allows
     * components to communicate with each other in a decoupled manner
     * by sending and handling events.
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Refreshes the application routes by retrieving the latest route details from the database.
     * This method publishes a {@link RefreshRoutesEvent} to notify other components about
     * the refresh operation, enabling them to update their state or behavior accordingly.
     * It uses the {@link ApplicationEventPublisher} to broadcast the event and generates
     * an informational log message indicating the initiation of the refresh process.
     */
    @Scheduled(fixedRate = 60000)
    public void refresh() {
        log.info("Refreshing routes from db");
        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
