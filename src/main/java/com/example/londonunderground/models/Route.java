package com.example.londonunderground.models;

import java.util.List;

public class Route {
    // FIELDS
    private List<Station> path; // List of stations in the route
    private int stops; // Number of stops in the route
    private double distance; // Total distance of the route
    private int lineChanges; // Number of line changes in the route
    private double lineChangePenalty; // Line change penalty for the route

    // CONSTRUCTORS
    public Route(List<Station> path, int stops) {
        this.path = path;
        this.stops = stops;
        this.distance = 0; // Default value
        this.lineChanges = getLineChanges(); // Calculate the number of line changes
    }

    public Route(List<Station> path, double distance) {
        this.path = path;
        this.distance = distance;
        this.stops = 0; // Default value
        this.lineChanges = getLineChanges(); // Calculate the number of line changes
    }

    public Route(List<Station> path, int lineChanges, double distance, double lineChangePenalty) {
        this.path = path;
        this.lineChanges = lineChanges;
        this.distance = distance;
        this.stops = path.size() - 1; // Number of stops is one less than the number of stations
        this.lineChangePenalty = lineChangePenalty; // Set the lineChangePenalty
    }

    // GETTERS
    public List<Station> getPath() {
        return path;
    }

    public int getStops() {
        return stops;
    }

    public double getDistance() {
        return distance;
    }

    public int getLineChanges() {
        return lineChanges;
    }

    public double getLineChangePenalty() {
        return lineChangePenalty;
    }

}
