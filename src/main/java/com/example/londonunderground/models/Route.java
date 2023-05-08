package com.example.londonunderground.models;

import java.util.List;

public class Route {
    // A list of Station objects that represent the path of the route
    private List<Station> path;
    // An integer that represents the number of stops in the route
    private int stops;

    // Constructor to initialize the Route object with a path and number of stops
    public Route(List<Station> path, int stops) {
        this.path = path;
        this.stops = stops;
    }

    // Getter method to retrieve the path of the route
    public List<Station> getPath() {
        return path;
    }

    // Getter method to retrieve the number of stops in the route
    public int getStops() {
        return stops;
    }
}

