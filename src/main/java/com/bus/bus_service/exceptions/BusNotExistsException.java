package com.bus.bus_service.exceptions;

public class BusNotExistsException extends RuntimeException {
    private Long busId;
    public BusNotExistsException(Long busId) {
        super("Bus with ID " + busId + " not exists");
        this.busId = busId;
    }

    public Long getBusId() {
        return busId;
    }
}
