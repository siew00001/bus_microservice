package com.bus.bus_service.service;

import com.bus.bus_service.dto.Bus;
import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;
import com.bus.bus_service.exceptions.BusIsCurrentlyInUseException;
import com.bus.bus_service.exceptions.BusNotExistsException;
import com.bus.bus_service.exceptions.BusNumberAlreadyExistsException;
import com.bus.bus_service.exceptions.BusNumberNotFoundException;
import com.bus.bus_service.repository.BusRepository;
import com.bus.bus_service.repository.RouteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BusService {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public BusService(BusRepository busRepository, RouteRepository routeRepository){
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    public Optional<BusEntity> createBus(BusEntity bus){
        if(busRepository.existsByBusNumber(bus.getBusNumber())){
            throw new BusNumberAlreadyExistsException(bus.getBusNumber().longValue());
        }
        return Optional.of(busRepository.save(bus));
    }
    /*public Optional<BusEntity> updateBus(Long busId, BusEntity updatedBus) {
        if(busRepository.existsById(busId)){
            Optional<BusEntity> checkBus = busRepository.findBusEntityByBusNumber(updatedBus.getBusNumber());
            if(checkBus.isPresent()){
                if(checkBus.get().getBusId().longValue() == busId){
                    updatedBus.setBusId(busId);
                    return Optional.of(busRepository.save(updatedBus));
                }
                throw new BusNumberAlreadyExistsException(updatedBus.getBusNumber().longValue());
            }
        }
        throw new BusNotFoundException(busId);
    }*/
    public Optional<BusEntity> updateBus(Long busId, BusEntity updatedBus) {
        if(busRepository.existsById(busId)){
            Optional<BusEntity> checkBus = busRepository.findBusEntityByBusNumber(updatedBus.getBusNumber());
            if(checkBus.isPresent()){
                if(!checkBus.get().getBusId().equals(busId)){
                    throw new BusNumberAlreadyExistsException(updatedBus.getBusNumber().longValue());
                }
                updatedBus.setBusId(busId);
                return Optional.of(busRepository.save(updatedBus));
            }
            updatedBus.setBusId(busId);
            return Optional.of(busRepository.save(updatedBus));
        }
        throw new BusNotExistsException(busId);
    }

    public boolean deleteBusById(Long id){
        Optional<BusEntity> bus = busRepository.findById(id);
        if(bus.isPresent()){
            List<RouteEntity> routes = routeRepository.getRouteEntityByBusesContaining(List.of(bus.get()));
            if(!routes.isEmpty()){
                throw new BusIsCurrentlyInUseException(bus.get().getBusId(), routes);
            }else {
                busRepository.deleteById(id);
                return true;
            }
        }else{
            throw new BusNotExistsException(id);
        }
    }

    public boolean deleteBusByBusNumber(Integer busNumber){
        if(busRepository.existsByBusNumber(busNumber)){
            busRepository.deleteByBusNumber(busNumber);
            return true;
        }else{
            return false;
        }
    }

    public List<BusEntity> getAllBusses(){
        return busRepository.findAll();
    }
    public Optional<BusEntity> getBusById(Long id){
        return busRepository.findById(id);
    }

    public Optional<BusEntity> getBusByBusNumber(Integer busNumber){
        Optional<BusEntity> busEntityOptional = busRepository.findBusEntityByBusNumber(busNumber);
        if(busEntityOptional.isPresent()){
            return busEntityOptional;
        }
        throw new BusNumberNotFoundException(busNumber.longValue());
    }
}
