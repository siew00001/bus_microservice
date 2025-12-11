package com.bus.bus_service.entities;

import jakarta.persistence.*;

import java.util.List;


@Entity
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long routeId;
    private String start;
    private String destination;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "route_bus",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "bus_id")
    )
    private List<BusEntity> buses;

    public RouteEntity(){}

    public RouteEntity(String start, String destination, List<BusEntity> buses){
        this.start = start;
        this.destination = destination;
        this.buses = buses;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public List<BusEntity> getBuses() {
        return buses;
    }

    public void setBuses(List<BusEntity> buses) {
        this.buses = buses;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
