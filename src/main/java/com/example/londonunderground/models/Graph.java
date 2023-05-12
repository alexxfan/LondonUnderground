package com.example.londonunderground.models;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

public class Graph implements Initializable {
    // A static graph object to represent the graph
    public static Graph graph;
    // A station adjacency list to represent vertices on the graph
    private Map<Station, List<Station>> adjacencyList;

    // Constructor to initialize the Graph object with an empty adjacency list
    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    // Method to get the set of neighbors of a given station
    public Set<Station> getNeighbors(Station station) {
        return station.getNeighbors().keySet();
    }

    // Method to find the shortest path between two stations using BFS
    public Route findShortestPath(Station start, Station end) {
        // Initialize a previous map, queue, and visited set
        Map<Station, Station> previous = new HashMap<>();
        Queue<Station> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();

        // Add the start station to the queue and visited set
        queue.add(start);
        visited.add(start);

        // While the queue is not empty
        while (!queue.isEmpty()) {
            // Get the next station from the queue
            Station current = queue.poll();

            // If we have reached the end station, build the path and return it
            if (current.equals(end)) {
                List<Station> path = new ArrayList<>();
                int stops = 0;

                Station pathStation = end;
                while (pathStation != null) {
                    path.add(0, pathStation);
                    pathStation = previous.get(pathStation);
                    stops++;
                }

                return new Route(path, stops - 1);
            }

            // Otherwise, get the neighbors of the current station and explore them
            Set<Station> neighbors = getNeighbors(current);
            for (Station neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    previous.put(neighbor, current);
                }
            }
        }

        // If no path is found, return null
        return null;
    }

    // Method to initialize the Graph object
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Set the static graph object to this
        graph = this;
    }
}
