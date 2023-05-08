package com.example.londonunderground.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Station {
    //FIELDS

    // The name of the station
    private String stationName;
    // The x-coordinate of the station
    private double x;
    // The y-coordinate of the station
    private double y;
    // A map of neighboring stations and their distances
    private Map<Station, Double> neighbors;

    private List<Double> distances;


    // CONSTRUCTOR
    public Station(String stationName, double x, double y) {
        // Initialize the station with a name, x-coordinate, and y-coordinate
        this.stationName = stationName;
        this.x = x;
        this.y = y;
        // Initialize the map of neighbors as an empty HashMap
        this.neighbors = new HashMap<>();
        this.distances = new ArrayList<>();
    }


    // GETTERS
    public String getStationName() {
        // Get the name of the station
        return stationName;
    }

    public double getX() {
        // Get the x-coordinate of the station
        return x;
    }

    public double getY() {
        // Get the y-coordinate of the station
        return y;
    }

    public Map<Station, Double> getNeighbors() {
        // Get the map of neighboring stations and their distances
        return neighbors;
    }


    // SETTERS
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setNeighbors(Map<Station, Double> neighbors) {
        this.neighbors = neighbors;
    }

    public void addNeighbor(Station neighbor, double distance) {
        // Add a neighboring station and its distance to the map of neighbors
        this.neighbors.put(neighbor, distance);
        distances.add(distance);
    }

    @Override
    public String toString() {
        String neighborNames = neighbors.keySet().stream()
                .map(Station::getStationName)
                .collect(Collectors.joining(", "));

        return "Station: " + "Name: " + stationName + ", xCoordinate: " + x + ", yCoordinate: =" + y + ", neighbouring stations: " + neighborNames;
    }
}