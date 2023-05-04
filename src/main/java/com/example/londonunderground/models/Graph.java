package com.example.londonunderground.models;

import java.util.*;

public class Graph {
    // An adjacency list to represent the graph
    private Map<Station, List<Station>> adjacencyList;

    public Graph() {
        // Initialize the adjacency list as a HashMap
        this.adjacencyList = new HashMap<>();
    }

    public void addStation(Station station) {
        // Add a station to the graph by putting it in the adjacency list with an empty list of neighbors
        this.adjacencyList.putIfAbsent(station, new ArrayList<>());
    }

    public void addEdge(Station station1, Station station2) {
        // Add an edge to the graph by adding each station to the other station's list of neighbors
        this.adjacencyList.get(station1).add(station2);
        this.adjacencyList.get(station2).add(station1);
    }

    public List<Station> getNeighbors(Station station) {
        // Get the list of neighbors of a given station
        return this.adjacencyList.get(station);
    }

    public Set<Station> getStations() {
        // Get the set of all stations in the graph
        return this.adjacencyList.keySet();
    }
}
