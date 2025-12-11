package com.bus.bus_service.service;

import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.exceptions.BusNotExistsException;
import com.bus.bus_service.exceptions.BusNumberAlreadyExistsException;
import com.bus.bus_service.exceptions.BusNumberNotFoundException;
import com.bus.bus_service.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BusService.class)
public class BusServiceTests {
    @Autowired
    BusRepository busRepository;
    @Autowired
    BusService busService;
    BusEntity busEntity;

    @BeforeEach
    void setBusEntity() {
        //given entity
        //ID is set automatically
        busEntity = new BusEntity(100, "name", 10f, 20f);
    }

    @Test
    void testCreateBus() {
        //test if entity gets returned
        assertTrue(busService.createBus(busEntity).isPresent());
        //test if bus with same id gets caught
        assertThrows(BusNumberAlreadyExistsException.class , () -> busService.createBus(busEntity).isEmpty());
        //test if entity gets saved in db
        assertEquals("name", busRepository.findAll().getFirst().getName());
    }

    @Test
    void testUpdateBus() {
        //save bus with number 100
        busRepository.save(busEntity);
        //update bus
        busEntity.setName("new name");

        //try to update non-existent bus
        assertThrows(BusNotExistsException.class, () -> busService.updateBus(50L, busEntity).isEmpty());
        //try to update existing bus
        assertTrue(busService.updateBus(busEntity.getBusId(), busEntity).isPresent());
        //check if update worked
        assertEquals("new name", busRepository.findAll().getFirst().getName());
        //check if there is still only one bus in the db
        assertEquals(1, busRepository.findAll().size());
    }

    @Test
    void testDeleteBusById(){
        //save bus
        busRepository.save(busEntity);
        //get busID
        Long busID = busRepository.findAll().getFirst().getBusId();
        //try to delete non-existent bus
        assertThrows(BusNotExistsException.class , () -> busService.deleteBusById(50L));
        //try to delete existing bus
        assertTrue(busService.deleteBusById(busID));
    }

    @Test
    void testDeleteBusByBusNumber(){
        //save bus with number 100
        busRepository.save(busEntity);
        //get bus number
        Integer busNumber = busRepository.findAll().getFirst().getBusNumber();
        //try to delete non-existent bus
        assertFalse(busService.deleteBusByBusNumber(50));
        //try to delete existing bus
        assertTrue(busService.deleteBusByBusNumber(busNumber));
    }

    @Test
    void testGetAllBusses() {
        //add 3 busses
        BusEntity busEntity2 = new BusEntity();
        BusEntity busEntity3 = new BusEntity();
        //save all buses
        busRepository.save(busEntity);
        busRepository.save(busEntity2);
        busRepository.save(busEntity3);
        //compare amount of added entities to amount getAllBuses returns
        assertEquals(3, busService.getAllBusses().size());
    }

    @Test
    void testGetBusById() {
        //add bus
        busRepository.save(busEntity);
        //get id
        Long busId = busRepository.findAll().getFirst().getBusId();
        //try to get non-existent bus
        assertTrue(busService.getBusById(-1L).isEmpty());
        //try to get existing bus
        assertTrue(busService.getBusById(busId).isPresent());
    }

    @Test
    void testGetBusByBusNumber() {
        //add bus
        busRepository.save(busEntity);
        //bus number equals 100
        //try to get non-existent bus
        assertThrows(BusNumberNotFoundException.class, () -> busService.getBusByBusNumber(-1).isEmpty());
        //try to get existing bus
        assertTrue(busService.getBusByBusNumber(100).isPresent());
    }
}
