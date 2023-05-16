package com.example.londonunderground.models;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

import static com.example.londonunderground.controller.MainController.mainControl;

public class Graph implements Initializable {
    // A static graph object to represent the graph
    public static Graph graph;

    // A station adjacency list to represent vertices on the graph
    private Map<Station, List<Station>> adjacencyList;

    // Method to get the set of neighbors of a given station aka adjacent stations
    public Set<Station> getNeighbors(Station station) {
        return station.getNeighbors().keySet();
    }

    // Method to find the shortest path between two stations using Breadth-First Search (BFS)
    public Route findShortestPathBFS(Station start, Station end) {
        // Initialize data structures for previous stations, a queue, and a visited set
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

                // Build the path and calculate the number of stops by backtracking through the previous stations
                Station pathStation = end;
                while (pathStation != null) {
                    path.add(0, pathStation);
                    pathStation = previous.get(pathStation);
                    stops++;
                }

                // Return the route with the path and number of stops
                return new Route(path, stops - 1);
            }

            // Otherwise, explore the neighbors of the current station
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
            // Get the next station with the smallest distance from the priority queue
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
                // Return the route with the path and total distance
                return new Route(path, totalDistance);
            }
            // Otherwise, explore the neighbors of the current station
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


    // Method to find the shortest path between two stations using Dijkstra's algorithm with a line change penalty
    public Route findShortestPathDijkstraWithPenalty(Set<Station> allStations, Station start, Station end, double lineChangePenalty) {
        // Initialize data structures for distances, previous stations, current lines, line changes, costs, and a priority queue
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previous = new HashMap<>();
        Map<Station, Line> currentLines = new HashMap<>();
        Map<Station, Integer> lineChanges = new HashMap<>();
        Map<Station, Double> costs = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>((a, b) -> Double.compare(costs.get(a), costs.get(b)));

        // Initialize distances, line changes, and costs to infinity for all stations except the start station
        for (Station station : allStations) {
            distances.put(station, Double.MAX_VALUE);
            lineChanges.put(station, Integer.MAX_VALUE);
            costs.put(station, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        lineChanges.put(start, 0);
        costs.put(start, 0.0);

        // Add the start station to the queue
        queue.add(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            // If we have reached the end station, build the path and return it
            if (current.equals(end)) {
                List<Station> path = new ArrayList<>();
                double totalDistance = 0.0;
                int totalLineChanges = 0;

                // Build the path and calculate the total distance and line changes by backtracking through the previous stations
                for (Station station = end; station != null; station = previous.get(station)) {
                    path.add(0, station);
                    if (previous.get(station) != null) {
                        totalDistance += distances.get(station);
                        totalLineChanges += lineChanges.get(station);
                    }
                }

                // Return the route with the path, total line changes, total distance, and line change penalty
                return new Route(path, totalLineChanges, totalDistance, lineChangePenalty);
            }

            // Explore the neighbors of the current station
            for (Station neighbor : getNeighbors(current)) {
                double lineChangeCost = 0.0;

                // Check if a line change is required between the current station and the neighbor
                if (currentLines.get(current) != null && !mainControl.getCommonLines(current, neighbor).contains(currentLines.get(current))) {
                    lineChangeCost = lineChangePenalty;
                }

                // Calculate the distance without penalty and the line change count
                double distanceWithoutPenalty = distances.get(current) + current.getNeighbors().get(neighbor);
                int lineChangeCount = lineChanges.get(current) + (lineChangeCost > 0 ? 1 : 0);

                // Compute the cost as a weighted sum of distance and line change count
                double cost = distanceWithoutPenalty + lineChangeCount * lineChangePenalty;

                // If the computed cost is smaller than the recorded cost, update the distances, line changes, costs, previous stations, current lines, and add the neighbor to the queue
                if (cost < costs.get(neighbor)) {
                    distances.put(neighbor, distanceWithoutPenalty);
                    lineChanges.put(neighbor, lineChangeCount);
                    costs.put(neighbor, cost);
                    previous.put(neighbor, current);
                    currentLines.put(neighbor, mainControl.getCommonLines(current, neighbor).get(0));
                    queue.add(neighbor);
                }
            }
        }
        // If no path is found, return null
        return null;
    }

    // Method to find all the paths between two stations using Depth-First Search (DFS)
    public List<Route> findAllPathsDFS(Station start, Station end, int maxPaths) {
        // Create a list to store all the found paths
        List<Route> allPaths = new ArrayList<>();

        // Create a stack to keep track of the current path being explored
        Stack<Station> pathStack = new Stack<>();

        // Create a set to mark visited stations and avoid revisiting them
        Set<Station> visited = new HashSet<>();

        // Call the recursive helper function to find all paths from start to end
        findAllPathsDFSUtil(start, end, visited, pathStack, allPaths, maxPaths);

        // Return the list of all found paths
        return allPaths;
    }


    // A recursive function to find all paths from 'start' to 'end' using Depth-First Search (DFS)
    private void findAllPathsDFSUtil(Station start, Station end, Set<Station> visited, Stack<Station> pathStack, List<Route> allPaths, int maxPaths) {
        // Mark the current node as visited and add it to the path stack
        visited.add(start);
        pathStack.push(start);

        // If the current station is the destination, add the current path to the list of all paths
        if (start.equals(end)) {
            allPaths.add(new Route(new ArrayList<>(pathStack), pathStack.size() - 1));
            // Limit the number of paths to maxPaths
            if (allPaths.size() >= maxPaths) {
                return;
            }
        } else {
            // If the current station is not the destination, recursively explore its unvisited neighbors
            Set<Station> neighbors = getNeighbors(start);
            for (Station neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    findAllPathsDFSUtil(neighbor, end, visited, pathStack, allPaths, maxPaths);
                    // Limit the number of paths to maxPaths
                    if (allPaths.size() >= maxPaths) {
                        return;
                    }
                }
            }
        }

        // Remove the current station from the path stack and mark it as not visited
        pathStack.pop();
        visited.remove(start);
    }


    // Method to initialize the Graph object
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Set the static graph object to this
        graph = this;
    }
}
