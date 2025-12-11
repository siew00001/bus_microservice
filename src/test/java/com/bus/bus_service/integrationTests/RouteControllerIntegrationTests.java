package com.bus.bus_service.integrationTests;
import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;
import com.bus.bus_service.repository.BusRepository;
import com.bus.bus_service.repository.RouteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RouteControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BusRepository busRepository;

    @Test
    @Transactional
    void createNewRouteSuccess() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 1.5f, 50f);
        busRepository.save(busEntity);

        String routeJSON = """
            {
              "busNumber": [122],
              "start": "HTW Saar",
              "destination": "Rathaus"
            }
            """;

        mockMvc.perform(post("/api/v1/route")
                .content(routeJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.buses[0].busNumber").value(122))
            .andExpect(jsonPath("$.start").value("HTW Saar"))
            .andExpect(jsonPath("$.destination").value("Rathaus"));

        assertEquals(routeRepository.getRouteEntitiesByStart("HTW Saar").size(), 1);
        RouteEntity routeEntity = routeRepository.getRouteEntitiesByStart("HTW Saar").getFirst();
        assertEquals(routeEntity.getBuses().getFirst().getBusNumber(), 122);
        assertEquals(routeEntity.getStart(), "HTW Saar");
        assertEquals(routeEntity.getDestination(), "Rathaus");
    }

    @Test
    @Transactional
    void createNewRouteWithNotExistingBus() throws Exception {
        String routeJSON = """
            {
              "busNumber": [122],
              "start": "HTW Saar",
              "destination": "Rathaus"
            }
            """;

        mockMvc.perform(post("/api/v1/route")
                .content(routeJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(422));

        assertEquals(routeRepository.findAll().size(), 0);
    }

    @Test
    @Transactional
    void updateExistingRouteSuccess() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 1.5f, 50f);
        busRepository.save(busEntity);
        busRepository.save(busEntity);
        List<BusEntity> busEntityList = new ArrayList<>();
        busEntityList.add(busEntity);

        RouteEntity routeEntity = new RouteEntity("HTW Saar", "Rathaus", busEntityList);
        routeRepository.save(routeEntity);

        String routeJSON = """
            {
              "busNumber": [122],
              "start": "Rathaus",
              "destination": "HTW Saar"
            }
            """;

        mockMvc.perform(put("/api/v1/route/" + routeEntity.getRouteId())
                .content(routeJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.buses[0].busNumber").value(122))
            .andExpect(jsonPath("$.start").value("Rathaus"))
            .andExpect(jsonPath("$.destination").value("HTW Saar"));

        assertEquals(routeRepository.findAll().size(), 1);
        assertTrue(routeRepository.findById(routeEntity.getRouteId()).isPresent());
        RouteEntity route = routeRepository.findById(routeEntity.getRouteId()).get();
        assertEquals(route.getBuses().getFirst().getBusNumber(), 122);
        assertEquals(route.getStart(), "Rathaus");
        assertEquals(route.getDestination(), "HTW Saar");
    }

    @Test
    @Transactional
    void updateExistingRouteFailure() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 1.5f, 50f);
        busRepository.save(busEntity);
        List<BusEntity> busEntityList = new ArrayList<>();
        busEntityList.add(busEntity);

        RouteEntity routeEntity = new RouteEntity("HTW Saar", "Rathaus", busEntityList);
        routeRepository.save(routeEntity);

        String routeJSON = """
            {
              "busNumber": [124],
              "start": "Rathaus",
              "destination": "HTW Saar"
            }
            """;

        mockMvc.perform(put("/api/v1/route/" + routeEntity.getRouteId())
                .content(routeJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(422));

        assertEquals(routeRepository.findAll().size(), 1);
        assertTrue(routeRepository.findById(routeEntity.getRouteId()).isPresent());
        RouteEntity route = routeRepository.findById(routeEntity.getRouteId()).get();
        assertEquals(route.getBuses().getFirst().getBusNumber(), 122);
        assertEquals(route.getStart(), "HTW Saar");
        assertEquals(route.getDestination(), "Rathaus");
    }

    @Test
    @Transactional
    void deleteExistingRouteSuccess() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey",1.5f, 50f );
        busRepository.save(busEntity);

        RouteEntity routeEntity = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity));
        routeRepository.save(routeEntity);

        mockMvc.perform(delete("/api/v1/route/" + routeEntity.getRouteId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(routeRepository.findAll().size(), 0);
    }

    @Test
    @Transactional
    void deleteExistingRouteFailure() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 1.5f, 50f);
        busRepository.save(busEntity);

        RouteEntity routeEntity = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity));
        routeRepository.save(routeEntity);

        assertEquals(routeRepository.findAll().size(), 1);

        mockMvc.perform(delete("/api/v1/route/" + 2)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        assertEquals(routeRepository.findAll().size(), 1);
        assertTrue(routeRepository.findById(routeEntity.getRouteId()).isPresent());
    }

    @Test
    @Transactional
    void getAllRoutesSortedByPrice() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 1.5f, 30f);
        busRepository.save(busEntity);
        BusEntity busEntity2 = new BusEntity(127, "Harvey", 3.2f, 50f);
        busRepository.save(busEntity2);
        BusEntity busEntity3 = new BusEntity(124, "Harvey", 4.1f, 70f);
        busRepository.save(busEntity3);

        RouteEntity routeEntity = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity));
        routeRepository.save(routeEntity);
        RouteEntity routeEntity2 = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity2));
        routeRepository.save(routeEntity2);
        RouteEntity routeEntity3 = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity3));
        routeRepository.save(routeEntity3);

        assertEquals(routeRepository.findAll().size(), 3);

        mockMvc.perform(get("/api/v1/route?sort=true&criteria=price")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].buses[0].kmPrice").value(1.5))
            .andExpect(jsonPath("$[1].buses[0].kmPrice").value(3.2))
            .andExpect(jsonPath("$[2].buses[0].kmPrice").value(4.1));
    }

    @Test
    @Transactional
    void getRouteFilteredByPrice() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 1.5f, 30f);
        busRepository.save(busEntity);
        BusEntity busEntity2 = new BusEntity(127, "Harvey", 3.2f, 50f);
        busRepository.save(busEntity2);
        BusEntity busEntity3 = new BusEntity(124, "Harvey", 4.1f, 70f);
        busRepository.save(busEntity3);

        RouteEntity routeEntity = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity));
        routeRepository.save(routeEntity);
        RouteEntity routeEntity2 = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity2));
        routeRepository.save(routeEntity2);
        RouteEntity routeEntity3 = new RouteEntity("HTW Saar", "Rathaus", List.of(busEntity3));
        routeRepository.save(routeEntity3);

        assertEquals(routeRepository.findAll().size(), 3);

        mockMvc.perform(get("/api/v1/route?filter=true&criteria=price")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].buses[0].kmPrice").value(1.5));
    }
}
