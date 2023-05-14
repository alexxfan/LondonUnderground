package com.example.londonunderground.models;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

public class Graph implements Initializable {
    // A static graph object to represent the graph
    public static Graph graph;

    // A station adjacency list to represent vertices on the graph
    private Map<Station, List<Station>> adjacencyList;

    // Method to get the set of neighbors of a given station
    public Set<Station> getNeighbors(Station station) {
        return station.getNeighbors().keySet();
    }

    // Method to find the shortest path between two stations using BFS
    public Route findShortestPathBFS(Station start, Station end) {
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


    // Method to find the shortest path between two stations using Dijkstra's algorithm
    public Route findShortestPathDijkstra(Set<Station> allStations, Station start, Station end) {
        // Initialize data structures for distances, previous stations, and a priority queue
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previous = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>((a, b) -> Double.compare(distances.get(a), distances.get(b)));

        // Initialize the distances map with INFINITY distance for all stations, except for the start station, which has distance 0
        for (Station station : allStations) {
            distances.put(station, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);

        // Start the search from the start station
        queue.add(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            // If we have reached the end station, build the path and return it
            if (current.equals(end)) {
                List<Station> path = new ArrayList<>();
                double totalDistance = 0.0;

                // Build the path and calculate the total distance by backtracking through the previous stations
                for (Station station = end; station != null; station = previous.get(station)) {
                    path.add(0, station);
                    if (previous.get(station) != null) {
                        totalDistance += distances.get(station);
                    }
                }

                return new Route(path, totalDistance);
            }

            // Otherwise, get the neighbors of the current station and explore them
            for (Station neighbor : getNeighbors(current)) {
                // Calculate the distance from the start station to the neighbor through the current station
                double distance = distances.get(current) + current.getNeighbors().get(neighbor);

                // If the calculated distance is smaller than the previously recorded distance, update it
                if (distance < distances.get(neighbor)) {
                    distances.put(neighbor, distance);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
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
