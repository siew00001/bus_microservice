package com.bus.bus_service.repository;

import com.bus.bus_service.entities.BusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<BusEntity, Long> {
    boolean existsByBusNumber(Integer busNumber);
    Optional<BusEntity> findBusEntityByBusNumber(Integer busNumber);
    void deleteByBusNumber(Integer busNumber);

}
