package com.bus.bus_service.service;

import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;
import com.bus.bus_service.exceptions.BusNumberAlreadyContainedException;
import com.bus.bus_service.exceptions.BusNumbersNotExistsException;
import com.bus.bus_service.exceptions.RouteNotExistsException;
import com.bus.bus_service.repository.BusRepository;
import com.bus.bus_service.repository.RouteRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class RouteService {

    private final RouteRepository routeRepository;
    private final BusRepository busRepository;

    public RouteService(RouteRepository routeRepository, BusRepository busRepository){
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
    }

    public RouteEntity createRoute(RouteEntity routeEntity) {
        return routeRepository.save(routeEntity);
    }

    public Optional<RouteEntity> updateRoute(Long id, RouteEntity routeEntity) {
        if (routeRepository.existsById(id)) {
            routeEntity.setRouteId(id);
            return Optional.of(routeRepository.save(routeEntity));
        }
        throw new RouteNotExistsException(id);
    }

    public Optional<RouteEntity> addBusToRoute(RouteEntity routeEntity, BusEntity busEntity){
        List<BusEntity> busList = new ArrayList<>(routeEntity.getBuses());
        busList.removeIf(bus -> !Objects.equals(bus.getBusId(), busEntity.getBusId()));
        if(!busList.isEmpty()){
            throw new BusNumberAlreadyContainedException(routeEntity.getRouteId(), busEntity.getBusNumber().longValue());
        }
        routeEntity.getBuses().add(busEntity);
        return updateRoute(routeEntity.getRouteId(), routeEntity);
    }

    public boolean deleteRouteById(Long id) {
        Optional<RouteEntity> routeEntityOptional = routeRepository.findById(id);
        if (routeEntityOptional.isPresent()) {
            routeRepository.delete(routeEntityOptional.get());
            return true;
        } else {
            throw new RouteNotExistsException(id);
        }
    }

    public List<RouteEntity> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<RouteEntity> getRouteById(Long id) {
        Optional<RouteEntity> routeEntityOptional = routeRepository.findById(id);
        if(routeEntityOptional.isPresent()){
            return routeRepository.findById(id);
        }
        throw new RouteNotExistsException(id);
    }

    public List<RouteEntity> getRoutesByStart(String start) {
        return routeRepository.getRouteEntitiesByStart(start);
    }

    public List<RouteEntity> getRoutesByDestination(String destination) {
        return routeRepository.getRouteEntitiesByDestination(destination);
    }

    public List<RouteEntity> getRoutesByStartAndDestination(String start, String destination) {
        return routeRepository.getRouteEntitiesByStartAndDestination(start, destination);
    }

    public List<RouteEntity> filterRoutes(String criteria, List<RouteEntity> routes) {
        List<RouteEntity> filteredRoutes = new ArrayList<>();
        switch (criteria.toLowerCase()) {
            case "price": {
                double minPrice = routes.stream().mapToDouble(this::getMinPriceOfRouteEntity).min().orElse(-1);
                if(minPrice == -1){
                    return new ArrayList<>();
                }

                for (RouteEntity route : routes) {
                    List<BusEntity> buses = new ArrayList<>(route.getBuses());
                    buses.removeIf(bus -> bus.getKmPrice() > minPrice);

                    if(!buses.isEmpty()) {
                        route.setBuses(buses);
                        filteredRoutes.add(route);
                    }
                }
                break;
            }
            case "speed": {
                double maxSpeed = routes.stream().mapToDouble(this::getMaxSpeedOfRouteEntity).max().orElse(-1);
                if(maxSpeed == -1){
                    return new ArrayList<>();
                }

                for (RouteEntity route : routes) {
                    List<BusEntity> buses = new ArrayList<>(route.getBuses());
                    buses.removeIf(bus -> bus.getAverageSpeed() < maxSpeed);

                    if(!buses.isEmpty()) {
                        route.setBuses(buses);
                        filteredRoutes.add(route);
                    }
                }
                break;
            }
        }
        return filteredRoutes;
    }

    private Double getMinPriceOfRouteEntity(RouteEntity routeEntity){
        return routeEntity.getBuses().stream()
                .mapToDouble(bus -> bus.getKmPrice().doubleValue())
                .min()
                .orElse(Double.MAX_VALUE - 1);
    }
    private Double getMaxSpeedOfRouteEntity(RouteEntity routeEntity){
        return routeEntity.getBuses().stream()
                .mapToDouble(bus -> bus.getAverageSpeed().doubleValue())
                .max().orElse(-1);
    }

    public List<RouteEntity> sortRoutes(String criteria, List<RouteEntity> routes) {
        List<RouteEntity> slicedList = sliceRoutes(routes);
        return switch (criteria.toLowerCase()) {
            case "price" ->
                    slicedList.stream()
                            .sorted(Comparator.comparingDouble(route -> route.getBuses().getFirst().getKmPrice())).toList();
            case "speed" ->
                    slicedList.stream()
                            .sorted(Comparator.comparingDouble(route -> route.getBuses().getFirst().getAverageSpeed())).toList().reversed();
            default -> routes;
        };
    }

    /**
     * Slices the single bus entries in a RouteEntity Buslist into single Route entries
     * @param routes
     * @return A list of RouteEntities where each entity only have one bus
     */
    private List<RouteEntity> sliceRoutes(List<RouteEntity> routes) {
        List<RouteEntity> slicedEntities = new ArrayList<>();

        for (RouteEntity route : routes) {
            for(BusEntity bus : route.getBuses()) {
                RouteEntity slicedRoute = new RouteEntity();
                slicedRoute.setRouteId(route.getRouteId());
                slicedRoute.setStart(route.getStart());
                slicedRoute.setDestination(route.getDestination());
                slicedRoute.setBuses(new ArrayList<>(Collections.singletonList(bus)));
                slicedEntities.add(slicedRoute);
            }
        }
        return slicedEntities;
    }

    public Optional<RouteEntity> buildRoute(List<Integer> busNumbers, String start, String destination){
        List<BusEntity> buses = new ArrayList<>();
        for(Integer busNumber : busNumbers){
            Optional<BusEntity> busEntityOptional = busRepository.findBusEntityByBusNumber(busNumber);
            if(busEntityOptional.isPresent()){
                BusEntity busEntity = busEntityOptional.get();
                buses.add(busEntity);
            }
        }
        if(buses.size() < busNumbers.size()) {
            List<Integer> missingNumbers = new ArrayList<>(busNumbers);
            missingNumbers.removeAll(buses.stream().map(BusEntity::getBusNumber).toList());
            throw new BusNumbersNotExistsException(missingNumbers);
        }
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setBuses(buses);
        routeEntity.setStart(start);
        routeEntity.setDestination(destination);
        return Optional.of(routeEntity);
    }
}

