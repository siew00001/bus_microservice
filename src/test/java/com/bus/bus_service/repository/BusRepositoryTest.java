package com.bus.bus_service.repository;

import com.bus.bus_service.entities.BusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DataJpaTest
public class BusRepositoryTest {

    @Autowired
    BusRepository repository;

    @BeforeEach
    void setRepository() {
        //given entity
        //ID is automatically set
        BusEntity busEntity = new BusEntity(100, "A", 20f, 10f);

        //save
        repository.save(busEntity);
    }


    @Test
    void givenBusPersistsWhenSaved() {
        //get id
        Long entityID = repository.findAll().getFirst().getBusId();
        //retrieve entity
        Optional<BusEntity> retrievedBusEntity = repository.findById(entityID);
        //test if entity is present and if attributes match
        assertTrue(retrievedBusEntity.isPresent());
        assertEquals("A", retrievedBusEntity.get().getName());
        assertEquals(20f, retrievedBusEntity.get().getKmPrice());
    }

    @Test
    void testExistsByBusNumber() {
        //existing bus
        assertTrue(repository.existsByBusNumber(100));
        //non-existent bus
        assertFalse(repository.existsByBusNumber(50));
    }

    @Test
    void testFindBusEntityByBusNumber() {
        //existing bus
        Optional<BusEntity> retrievedBusEntity = repository.findBusEntityByBusNumber(100);
        assertTrue(retrievedBusEntity.isPresent());
        assertEquals(10f, retrievedBusEntity.get().getAverageSpeed());

        //non existent bus
        assertFalse(repository.findBusEntityByBusNumber(50).isPresent());
    }

    @Test
    void deleteByBusNumber() {
       //test if bus exists before deleting
       assertTrue(repository.findBusEntityByBusNumber(100).isPresent());

       //delete
       repository.deleteByBusNumber(100);

       //test if bus exists after deleting
       assertFalse(repository.findBusEntityByBusNumber(100).isPresent());
    }
}
