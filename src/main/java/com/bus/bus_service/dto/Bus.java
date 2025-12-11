package com.bus.bus_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

public record Bus (
    Long busId,
    @NotNull(message = "busNumber cannot be null")
    @Positive(message = "busNumber must be positive")
    Integer busNumber,

    @NotNull(message = "Name cannot be null")
    String name,

    @NotNull(message = "kmPrice cannot be null")
    @Positive(message = "kmPrice must be positive")
    Float kmPrice,

    @NotNull(message = "averageSpeed cannot be null")
    @Positive(message = "averageSpeed must be positive")
    Float averageSpeed
) implements Serializable { }
