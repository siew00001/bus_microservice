package com.bus.bus_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RouteCreationDTO (
        @NotNull(message = "Start cannot be empty")
        String start,
        @NotNull(message = "Destination cannot be empty")
        String destination,
        @NotNull(message = "Bus Number cannot be empty")
        List<Integer> busNumber

) {}
