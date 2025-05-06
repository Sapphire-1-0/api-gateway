package com.brihaspathee.sapphire.route;

import com.brihaspathee.sapphire.domain.entity.Route;
import com.brihaspathee.sapphire.domain.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, February 2025
 * Time: 3:48 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.route
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SapphireRouteDefinitionRepository implements RouteDefinitionRepository {

    /**
     * Represents the repository used for performing database operations related to
     * the Route entity. It provides methods to retrieve and manage routing data,
     * including querying enabled routes and other CRUD operations inherited from JpaRepository.
     */
    private final RouteRepository routeRepository;


    /**
     * Retrieves the list of active route definitions from the route repository.
     *
     * @return a Flux containing the active route definitions.
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        log.info("Retrieving Routes");
        List<RouteDefinition> routeDefinitions = routeRepository.findByEnabledTrue()
                .stream()
                .map(this::convertToRouteDefinition)
                .toList();
        return Flux.fromIterable(routeDefinitions);
    }

    /**
     * Persists the provided route definition.
     *
     * @param route the Mono emitting the route definition to be saved
     * @return a Mono indicating the completion of the save operation
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return Mono.empty();
    }

    /**
     * Deletes the resource associated with the given route ID.
     *
     * @param routeId a {@link Mono} emitting the route ID of the resource to be deleted
     * @return a {@link Mono} signaling when the delete operation has completed
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.empty();
    }

    /**
     * Converts a given Route entity into a RouteDefinition object.
     * This method maps the route's ID, URI, predicates, and filters into a format
     * that is useful for defining routing configurations.
     *
     * @param route the Route entity containing routing information,
     *              including route ID, URI, predicates, and filters
     * @return a RouteDefinition object populated with data from the provided Route entity
     */
    private RouteDefinition convertToRouteDefinition(Route route) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(route.getRouteId());
        routeDefinition.setUri(URI.create(route.getUri()));

        // Add Predicates
        List<PredicateDefinition> predicates = new ArrayList<>();
        predicates.add(new PredicateDefinition("Path="+route.getPredicates().trim()));
//        predicates.add(new PredicateDefinition("Path=/api/v1/sapphire/mms/secured/member/**"));
//        for(String predicate : route.getPredicates().split(",")) {
//            PredicateDefinition predicateDefinition =
//                    new PredicateDefinition("Path="+predicate.trim());
//            predicates.add(predicateDefinition);
//
//        }
        routeDefinition.setPredicates(predicates);

        // Add filters
        if(route.getFilters() != null) {
            List<FilterDefinition> filters = new ArrayList<>();
            filters.add(new FilterDefinition(route.getFilters().trim()));
//            for(String filter : route.getFilters().split(",")) {
//                FilterDefinition filterDefinition =
//                        new FilterDefinition(filter.trim());
//                filters.add(filterDefinition);
//            }
            routeDefinition.setFilters(filters);
        }
        // routeDefinition.setOrder((route.getIsSecured() ? -1 : 0));
        log.info("Route Definition: " + routeDefinition);
        return routeDefinition;
    }
}
