package com.bus.bus_service.exceptions;

import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;

import java.util.ArrayList;
import java.util.List;

public class BusIsCurrentlyInUseException extends RuntimeException {
    private Long busId;
    private final List<RouteEntity> routes;
    public BusIsCurrentlyInUseException(Long busId, List<RouteEntity> routes) {
        super("The bus with the id \n" + busId + "\" is already in use");
        this.busId = busId;
        this.routes = routes;
    }

    public List<Long> getRouteIds() {
        List<Long> routeIds = new ArrayList<>();
        routes.forEach(route -> routeIds.add(route.getRouteId()));
        return routeIds;
    }

    public Long getBusId() {
        return busId;
    }
}
