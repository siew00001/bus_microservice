package com.bus.bus_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public record Route(
    Long routeId,
    @NotNull(message = "Start cannot be null")
    @Valid
    String start,
    @NotNull(message = "Destination cannot be null")
    @Valid
    String destination,
    @NotNull (message = "Bus cannot be null")
    @Valid
    List<Bus> buses

) implements Serializable {}