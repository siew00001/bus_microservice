package com.bus.bus_service.integrationTests;
import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.repository.BusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
public class BusControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BusRepository busRepository;

    @Test
    @Transactional
    void createNewBusSuccess() throws Exception {
        String busJSON = """
            {
              "busNumber": 122,
              "name": "Harvey",
              "kmPrice": 1.50,
              "averageSpeed": 50
            }
            """;

        mockMvc.perform(post("/api/v1/bus")
                .content(busJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.busNumber").value(122))
            .andExpect(jsonPath("$.name").value("Harvey"))
            .andExpect(jsonPath("$.kmPrice").value(1.50))
            .andExpect(jsonPath("$.averageSpeed").value(50));


        assertTrue(busRepository.findBusEntityByBusNumber(122).isPresent());
        BusEntity busEntity = busRepository.findBusEntityByBusNumber(122).get();
        assertEquals(busEntity.getBusNumber(), 122);
        assertEquals(busEntity.getName(), "Harvey");
        assertEquals(busEntity.getKmPrice(), 1.50f);
        assertEquals(busEntity.getAverageSpeed(), 50f);
    }

    @Test
    @Transactional
    void createNewBusFailure() throws Exception {

        // invalid JSON format
        String busJSON = """
            {
              "busNummer": 122,
              "speed": 3
            }
            """;

        mockMvc.perform(post("/api/v1/bus")
                .content(busJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        assertTrue(busRepository.findAll().isEmpty());
    }

    @Test
    @Transactional
    void createNewBusWhichAlreadyExists() throws Exception {
        String busJSON = """
            {
              "busNumber": 122,
              "name": "Harvey",
              "kmPrice": 1.50,
              "averageSpeed": 50
            }
            """;

        mockMvc.perform(post("/api/v1/bus")
                .content(busJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/bus")
                .content(busJSON).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(409));

        assertEquals(busRepository.findAll().size(), 1);
        assertTrue(busRepository.findBusEntityByBusNumber(122).isPresent());
        BusEntity busEntity = busRepository.findBusEntityByBusNumber(122).get();
        assertEquals(busEntity.getBusNumber(),122);
        assertEquals(busEntity.getName(),"Harvey");
        assertEquals(busEntity.getKmPrice(), 1.50f);
        assertEquals(busEntity.getAverageSpeed(),50f);
    }

    @Test
    @Transactional
    void deleteExistingBus() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 50f, 1.5f);
        busRepository.save(busEntity);

        assertTrue(busRepository.findBusEntityByBusNumber(122).isPresent());

        mockMvc.perform(delete("/api/v1/bus/" + busEntity.getBusId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertTrue(busRepository.findAll().isEmpty());
    }

    @Test
    @Transactional
    void deleteNotExistingBusFailure() throws Exception {
        BusEntity busEntity = new BusEntity(122, "Harvey", 50f, 1.5f);
        busRepository.save(busEntity);

        assertEquals(busRepository.findAll().size(),1);
        assertTrue(busRepository.findBusEntityByBusNumber(122).isPresent());

        mockMvc.perform(delete("/api/v1/bus/123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        assertEquals(busRepository.findAll().size(), 1);
        assertTrue(busRepository.findBusEntityByBusNumber(122).isPresent());
    }

}
