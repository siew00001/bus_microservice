package com.bus.bus_service.repository;


import com.bus.bus_service.dto.Bus;
import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    List<RouteEntity> getRouteEntitiesByStart(String start);
    List<RouteEntity> getRouteEntitiesByDestination(String destination);
    List<RouteEntity> getRouteEntitiesByStartAndDestination(String start, String destination);
    List<RouteEntity> getRouteEntityByBusesContaining(List<BusEntity> buses);
}


