package com.bus.bus_service.exceptions;

public class RouteNotExistsException extends RuntimeException {
    private Long routeId;
    public RouteNotExistsException(Long routeId) {
        super("Route with ID " + routeId + " not exists");
        this.routeId = routeId;
    }

    public Long getRouteId() {
        return routeId;
    }
}
