package com.brihaspathee.sapphire.domain.repository;

import com.brihaspathee.sapphire.domain.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, February 2025
 * Time: 3:39 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.domain.repository
 * To change this template use File | Settings | File and Code Template
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    /**
     * Retrieves a list of Route entities where the 'enabled' field is set to true.
     *
     * @return a list of enabled Route entities
     */
    List<Route> findByEnabledTrue();
}
