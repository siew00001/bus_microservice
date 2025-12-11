package com.bus.bus_service.dto;

import com.bus.bus_service.entities.BusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BusMapper {
    Bus toDTO(BusEntity busEntity);
    @Mapping(target = "busId", ignore = true)
    BusEntity toEntity(Bus dto);
}
