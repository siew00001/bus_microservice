package com.bus.bus_service.repository;

import com.bus.bus_service.dto.BusMapper;
import com.bus.bus_service.dto.BusMapperImpl;
import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(BusMapperImpl.class)
public class RouteRepositoryTest {

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    BusRepository busRepository;

    @Autowired
    BusMapper busMapper;

    @BeforeEach
    void setRepository() {
        //create bus
        //id is set automatically
        BusEntity busEntity = new BusEntity(100, "name", 10f, 20f);

        //create routeEntity
        //id is set automatically
        RouteEntity routeEntity = new RouteEntity("start", "destination", List.of(busEntity));

        //save
        busRepository.save(busEntity);
        routeRepository.save(routeEntity);
    }

    @Test
    void givenRoutePersistsWhenSaved() {
        //get id
        Long entityID = routeRepository.findAll().getFirst().getRouteId();
        //retrieve entity
        Optional<RouteEntity> retrievedRouteEntity = routeRepository.findById(entityID);
        //check if entity is present and if attributes match
        assertTrue(retrievedRouteEntity.isPresent());
        assertEquals("start", retrievedRouteEntity.get().getStart());
        assertEquals("destination", retrievedRouteEntity.get().getDestination());
        assertEquals("name", retrievedRouteEntity.get().getBuses().getFirst().getName());
    }

    @Test
    void testGetRouteEntitiesByStart() {
        //existing route
        List<RouteEntity> retrievedRouteEntity = routeRepository.getRouteEntitiesByStart("start");
        assertFalse(retrievedRouteEntity.isEmpty());
        assertEquals(100, retrievedRouteEntity.getFirst().getBuses().getFirst().getBusNumber());

        //non existent route
        List<RouteEntity> nonExistentRouteEntity = routeRepository.getRouteEntitiesByStart("error");
        assertTrue(nonExistentRouteEntity.isEmpty());
    }

    @Test
    void testGetRouteEntitiesByDestination() {
        //existing route
        List<RouteEntity> retrievedRouteEntity = routeRepository.getRouteEntitiesByDestination("destination");
        assertFalse(retrievedRouteEntity.isEmpty());
        assertEquals(100, retrievedRouteEntity.getFirst().getBuses().getFirst().getBusNumber());

        //non existent route
        List<RouteEntity> nonExistentRouteEntity = routeRepository.getRouteEntitiesByDestination("error");
        assertTrue(nonExistentRouteEntity.isEmpty());
    }

    @Test
    void testGetRouteEntitiesByStartAndDestination() {
        //existing route
        List<RouteEntity> retrievedRouteEntity = routeRepository.getRouteEntitiesByStartAndDestination("start", "destination");
        assertFalse(retrievedRouteEntity.isEmpty());
        assertEquals(100, retrievedRouteEntity.getFirst().getBuses().getFirst().getBusNumber());

        //non existent route
        List<RouteEntity> nonExistentRouteEntity = routeRepository.getRouteEntitiesByStartAndDestination("error", "error");
        assertTrue(nonExistentRouteEntity.isEmpty());
    }
}
