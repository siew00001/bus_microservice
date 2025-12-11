package com.bus.bus_service.exceptions;

public class BusNumberAlreadyExistsException extends RuntimeException {
    private Long busNumber;
    public BusNumberAlreadyExistsException(Long busNumber) {
        super("Bus number " + busNumber + " is already in use by another bus.");
        this.busNumber = busNumber;
    }

    public Long getBusNumber() {
        return busNumber;
    }
}