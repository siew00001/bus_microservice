package com.bus.bus_service.service;

import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;
import com.bus.bus_service.exceptions.BusNumbersNotExistsException;
import com.bus.bus_service.exceptions.RouteNotExistsException;
import com.bus.bus_service.repository.BusRepository;
import com.bus.bus_service.repository.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({RouteService.class, BusService.class})
public class RouteServiceTests {

    @Autowired
    RouteService routeService;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    BusRepository busRepository;

    RouteEntity routeEntity;

    Long entityID;


    @BeforeEach
    void setRouteEntity() {

        //create bus, id is set automatically
        BusEntity busEntity = new BusEntity(100, "name", 10f, 20f);
        List<BusEntity> busEntities = new ArrayList<>();
        busEntities.add(busEntity);

        //given routeEntity, id is set automatically
        routeEntity = new RouteEntity("start", "destination", busEntities);

        //save bus in db
        busRepository.save(busEntity);

        //call createRoute()
        routeService.createRoute(routeEntity);

        //get entityID
        entityID = routeRepository.findAll().getFirst().getRouteId();
    }

    @Test
    void testCreateRoute(){
        //test if routeEntity exists in db
        assertFalse(routeRepository.findAll().isEmpty());
        assertEquals("start", routeRepository.findAll().getFirst().getStart());
    }

    @Test
    void testUpdateRoute() {
        //update routeEntity
        routeEntity.setStart("updated start");

        //for non existent id
        assertThrows(RouteNotExistsException.class, () -> routeService.updateRoute(123L, routeEntity).isEmpty());

        //for existing id
        assertTrue(routeService.updateRoute(entityID, routeEntity).isPresent());
        assertEquals("updated start", routeService.updateRoute(entityID, routeEntity).get().getStart());
    }

    @Test
    void testDeleteRouteByID() {
        //non existent ID
        assertThrows(RouteNotExistsException.class, () -> routeService.deleteRouteById(123L));

        //existing ID
        assertTrue(routeService.deleteRouteById(entityID));
        assertTrue(routeRepository.findAll().isEmpty());
    }

    @Test
    void testGetAllRoutes() {
        //test if size matches in service and repository
        assertEquals(routeRepository.findAll().size(), routeService.getAllRoutes().size());
    }

    @Test
    void testGetRouteByID() {
        //non existent ID
        assertThrows(RouteNotExistsException.class, () -> routeService.getRouteById(123L).orElse(null));

        //existing ID
        Optional<RouteEntity> routeEntityOptional = routeService.getRouteById(entityID);
        routeEntityOptional.ifPresent(entity -> assertEquals(10f, entity.getBuses().getFirst().getKmPrice()));
    }

    @Test
    void testGetRoutesByStart() {
        //non existent start
        assertTrue(routeService.getRoutesByStart("error").isEmpty());

        //existing start
        assertFalse(routeService.getRoutesByStart("start").isEmpty());
    }

    @Test
    void testGetRoutesByDestination() {
        //non existent destination
        assertTrue(routeService.getRoutesByDestination("error").isEmpty());

        //existing destination
        assertFalse(routeService.getRoutesByDestination("destination").isEmpty());
    }

    @Test
    void testGetRoutesByStartAndDestination() {
        //non existent start and destination
        assertTrue(routeService.getRoutesByStartAndDestination("error", "destination").isEmpty());

        //existing start and destination
        assertFalse(routeService.getRoutesByStartAndDestination("start", "destination").isEmpty());
    }

    @Test
    void testBuildRoute() {
        //invalid bus number
        assertThrows(
            BusNumbersNotExistsException.class,
            () -> routeService.buildRoute(List.of(50), "start", "destination").isEmpty());

        //valid bus number
        var routeEntityOptional = routeService.buildRoute(List.of(100), "start", "destination");
        assertTrue(routeEntityOptional.isPresent());
    }

    @Test
    void testFilterRoutes() {
        //create buses
        BusEntity bus1 = new BusEntity(100, "name", 10f, 10f);
        BusEntity bus2 = new BusEntity(100, "name", 20f, 20f);

        //create routes
        RouteEntity route1 = new RouteEntity("start", "destination", List.of(bus1));
        RouteEntity route2 = new RouteEntity("start", "destination", List.of(bus2));

        //create list
        List<RouteEntity> routes = List.of(route1,route2);

        //test filter
        List<RouteEntity> result = routeService.filterRoutes("speed", routes);
        assertEquals(1, result.size());
        assertSame(route2, result.getFirst());
    }

    @Test
    void testSortRoutes() {
        //create buses
        BusEntity bus1 = new BusEntity(100, "name", 10f, 10f);
        BusEntity bus2 = new BusEntity(100, "name", 20f, 20f);

        //create routes
        RouteEntity route1 = new RouteEntity("start", "destination", List.of(bus1));
        RouteEntity route2 = new RouteEntity("start", "destination", List.of(bus2));

        //create list
        List<RouteEntity> routes = List.of(route1,route2);

        //test sort
        List<RouteEntity> result = routeService.sortRoutes("speed", routes);
        assertEquals(route2.getRouteId(), result.getFirst().getRouteId());
        result = routeService.sortRoutes("price", routes);
        assertEquals(route1.getRouteId(), result.getFirst().getRouteId());
    }
}
