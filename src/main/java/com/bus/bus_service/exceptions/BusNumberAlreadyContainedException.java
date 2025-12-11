package com.bus.bus_service.exceptions;

public class BusNumberAlreadyContainedException extends RuntimeException {
    private Long busNumber;
    private Long routeId;
    public BusNumberAlreadyContainedException(Long routeId, Long busNumber) {
        super("The route with the id \"" + routeId + "\" already contains the bus with the bus number \"" + busNumber + "\"");
        this.busNumber = busNumber;
        this.routeId = routeId;
    }

    public Long getBusNumber() {
        return busNumber;
    }

    public Long getRouteId() {
        return routeId;
    }
}