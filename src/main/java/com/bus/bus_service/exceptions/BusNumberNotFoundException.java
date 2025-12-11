package com.bus.bus_service.exceptions;

public class BusNumberNotFoundException extends RuntimeException {
    private Long busNumber;
    public BusNumberNotFoundException(Long busNumber) {
        super("Bus with busNumber " + busNumber + " not found");
        this.busNumber = busNumber;
    }

    public Long getBusNumber() {
        return busNumber;
    }
}
