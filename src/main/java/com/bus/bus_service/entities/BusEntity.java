package com.bus.bus_service.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class BusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long busId;
    @Column(unique = true)
    private Integer busNumber;
    private String name;
    private Float kmPrice;
    private Float averageSpeed;
    @ManyToMany(mappedBy = "buses")
    private List<RouteEntity> routes;

    public BusEntity(){}

    public BusEntity(Integer busNumber, String name, Float kmPrice, Float averageSpeed) {
        this.busNumber = busNumber;
        this.name = name;
        this.kmPrice = kmPrice;
        this.averageSpeed = averageSpeed;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getKmPrice() {
        return kmPrice;
    }

    public void setKmPrice(Float kmPrice) {
        this.kmPrice = kmPrice;
    }

    public Float getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setBusNumber(Integer busNumber) {
        this.busNumber = busNumber;
    }

    public Integer getBusNumber() {
        return busNumber;
    }

    public List<RouteEntity> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteEntity> routes) {
        this.routes = routes;
    }
}
