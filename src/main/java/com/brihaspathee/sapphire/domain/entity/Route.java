package com.brihaspathee.sapphire.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, February 2025
 * Time: 3:24 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.domain.entity
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Builder
@Entity
@Table(name = "routes")
@AllArgsConstructor
@NoArgsConstructor
public class Route {

    /**
     * Represents the automatically generated unique identifier for an entity.
     * Serves as the primary key for the Route entity, ensuring uniqueness per record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routes_seq")
    private Integer id;

    /**
     * Represents the unique identifier of a route.
     * This field is required, must be unique across all records,
     * and determines the primary reference for the route.
     */
    @Column(name = "route_id", unique = true, nullable = false)
    private String routeId;

    /**
     * Represents the URI associated with a specific route.
     * This field is mandatory and is used to define the target URI
     * for the routing operation.
     */
    @Column(name = "uri", nullable = false)
    private String uri;

    /**
     * Represents the conditions or criteria that must be met for a specific route.
     * This field is stored as a text value in the database and is required
     * for determining the valid predicates for routing.
     */
    @Column(name = "predicates", nullable = false, columnDefinition = "TEXT")
    private String predicates;

    /**
     * Represents the filters applied to a specific route.
     * Stored as a text field in the database, this attribute contains
     * the filtering criteria or transformations associated with the route.
     */
    @Column(name = "filters", nullable = false, columnDefinition = "TEXT")
    private String filters;

    /**
     * Indicates whether the route is enabled or disabled.
     * This field is mandatory and is represented as a boolean value.
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

//    @Column(name = "is_secured")
//    private Boolean isSecured;
}
