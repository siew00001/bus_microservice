package com.bus.bus_service.dto;

import com.bus.bus_service.entities.RouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RouteMapper {
    Route toDTO(RouteEntity routeEntity);

    @Mapping(target = "routeId", ignore = true)
    RouteEntity toEntity(Route dto);
}

