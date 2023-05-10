package com.example.londonunderground.controller;

import com.example.londonunderground.models.Graph;
import com.example.londonunderground.models.Line;
import com.example.londonunderground.models.Route;
import com.example.londonunderground.models.Station;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.londonunderground.models.Graph.graph;

public class MainController implements Initializable {

    public static MainController mainControl;
    private Graph graph = new Graph();
    @FXML
    public ImageView zoneImage;
    @FXML
    public Button popMap, clearMap, shortestPathBFS;
    @FXML
    public MenuButton avoidStation, waypointStation, startStation, endStation;
    @FXML
    public AnchorPane mapPane;
    @FXML
    public ListView listView;





    private Station selectedStartStation;
    private Station selectedEndStation;
    private Circle startStationCircle;
    private Circle endStationCircle;
    private Circle startStationOuterRing;
    private Circle endStationOuterRing;





    public void populateMap(ActionEvent actionEvent) {
        // Read the CSV data from the file
        Path path = Paths.get("src/main/resources/CSV/zone1Corrected.csv");
        String csvData = "";
        try {
            csvData = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Line> lines = createLinesAndStationsFromCSV(csvData);
        // Iterate over the lines and print their stations
        for (Map.Entry<String, Line> entry : lines.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().getColor());
            for (Station station : entry.getValue().getStations()) {
                System.out.println("\t" + station.getStationName() + " (" + station.getX() + "," + station.getY() + ")");
            }
            System.out.println("\n");
        }

        populateMenuButtons(lines);
    }

    private Map<String, Line> createLinesAndStationsFromCSV(String csvData) {
        Map<String, Line> lines = new HashMap<>();
        Map<String, Station> stations = new HashMap<>();

        // Split the csvData by newline character to get individual lines
        String[] csvLines = csvData.split("\n");

        // Loop through each line in the CSV
        for (String line : csvLines) {
            // Split the line by comma
            String[] values = line.split(",");

            // Check if the line has the correct number of values
            if (values.length != 5) {
                System.err.println("Invalid line in CSV: " + line);
                continue;
            }

            // Extract values from the split line
            String lineName = values[0].trim();
            String stationName = values[1].trim();
            String color = values[2].trim();
            double x = Double.parseDouble(values[3].trim());
            double y = Double.parseDouble(values[4].trim());

            // Create or retrieve the Line object
            Line lineObj = lines.get(lineName);
            if (lineObj == null) {
                lineObj = new Line(lineName, color);
                lines.put(lineName, lineObj);
            }

            // Create or retrieve the Station object
            Station stationObj = stations.get(stationName);
            if (stationObj == null) {
                stationObj = new Station(stationName, x, y);
                stations.put(stationName, stationObj);
            }

            // Add the station to the line
            lineObj.addStation(stationObj);

            // Get the previous station on the line, if it exists
            List<Station> currentLineStations = lineObj.getStations();
            if (currentLineStations.size() > 1) {
                Station previousStation = currentLineStations.get(currentLineStations.size() - 2);

                // Calculate the distance between the current and previous stations
                double distance = Math.sqrt(Math.pow(stationObj.getX() - previousStation.getX(), 2)
                        + Math.pow(stationObj.getY() - previousStation.getY(), 2));

                // Add the current station as a neighbor to the previous station
                previousStation.addNeighbor(stationObj, distance);

                // Add the previous station as a neighbor to the current station
                stationObj.addNeighbor(previousStation, distance);
            }
        }

        return lines;
    }

    private void populateMenuButtons(Map<String, Line> lines) {
        Set<Station> uniqueStationsSet = new HashSet<>();
        // Loop through each Line object in the lines Map
        for (Line line : lines.values()) {
            // Loop through each Station object in the Line's stations
            // Add the station to the uniqueStations set
            uniqueStationsSet.addAll(line.getStations());
        }
        // Convert the Set to a List
        List<Station> uniqueStationsList = new ArrayList<>(uniqueStationsSet);
        // Sort the list alphabetically by station names
        uniqueStationsList.sort(Comparator.comparing(Station::getStationName));
        // Add the stationMenuItems to all the MenuButtons
        avoidStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
        waypointStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
        startStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
        endStation.getItems().addAll(createStationMenuItems(uniqueStationsList));
    }

    private List<MenuItem> createStationMenuItems(List<Station> stations) {
        List<MenuItem> stationMenuItems = new ArrayList<>();
        for (Station station : stations) {
            MenuItem menuItem = new MenuItem(station.getStationName());
            menuItem.setOnAction(e -> handleStationMenuItemClicked(e, station));
            stationMenuItems.add(menuItem);
        }
        return stationMenuItems;
    }

    private void handleStationMenuItemClicked(ActionEvent event, Station station) {
        MenuItem clickedMenuItem = (MenuItem) event.getSource();
        MenuButton parentMenuButton = (MenuButton) clickedMenuItem.getParentPopup().getOwnerNode();

        // Set the selected station as the text of the parent MenuButton
        parentMenuButton.setText(station.getStationName());

        if (parentMenuButton == startStation) {
            selectedStartStation = station;
            drawStartStationCircle(station);
        } else if (parentMenuButton == endStation) {
            selectedEndStation = station;
            drawEndStationCircle(station);
        }
    }


    // This method calculates the actual coordinates of a station on the mapPane based on its relative coordinates and the scale of the map image
    private Point2D calculateActualCoordinates(Station station) {
        double scaleX = zoneImage.getBoundsInLocal().getWidth() / zoneImage.getImage().getWidth();
        double scaleY = zoneImage.getBoundsInLocal().getHeight() / zoneImage.getImage().getHeight();

        double actualX = station.getX() * scaleX;
        double actualY = station.getY() * scaleY;

        return new Point2D(actualX, actualY);
    }

    // This method creates a circle and outer ring for a station with a specified color, based on its actual coordinates
    private void createStationCircle(Circle innerCircle, Circle outerRing, Station station, Color color) {
        // Calculate the actual coordinates of the station on the mapPane
        Point2D actualCoordinates = calculateActualCoordinates(station);

        // Set the center and radius of the inner circle
        innerCircle.setCenterX(actualCoordinates.getX());
        innerCircle.setCenterY(actualCoordinates.getY());
        innerCircle.setRadius(5);
        innerCircle.setFill(color);

        // Set the center, radius, and color of the outer ring
        outerRing.setCenterX(actualCoordinates.getX());
        outerRing.setCenterY(actualCoordinates.getY());
        outerRing.setRadius(8); // Slightly larger radius for the outer ring
        outerRing.setFill(Color.TRANSPARENT);
        outerRing.setStroke(color);
        outerRing.setStrokeWidth(2);
    }


    // This method draws a blue circle and outer ring around the selected start station
    private void drawStartStationCircle(Station station) {
        // Remove any existing start station circle and outer ring from the mapPane
        if (startStationCircle != null) {
            mapPane.getChildren().removeAll(startStationCircle, startStationOuterRing);
        }
        // Create a new circle and outer ring with the specified station and color
        startStationCircle = new Circle();
        startStationOuterRing = new Circle();
        createStationCircle(startStationCircle, startStationOuterRing, station, Color.BLUE);
        // Add the new circle and outer ring to the mapPane
        mapPane.getChildren().addAll(startStationCircle, startStationOuterRing);
    }

    // This method draws a red circle and outer ring around the selected end station
    private void drawEndStationCircle(Station station) {
        // Remove any existing end station circle and outer ring from the mapPane
        if (endStationCircle != null) {
            mapPane.getChildren().removeAll(endStationCircle, endStationOuterRing);
        }
        // Create a new circle and outer ring with the specified station and color
        endStationCircle = new Circle();
        endStationOuterRing = new Circle();
        createStationCircle(endStationCircle, endStationOuterRing, station, Color.RED);
        // Add the new circle and outer ring to the mapPane
        mapPane.getChildren().addAll(endStationCircle, endStationOuterRing);
    }



    // This method draws a line between each pair of adjacent stations in the shortest path
    private void drawShortestPath(Route shortestRoute) {
        // Remove any existing lines from the mapPane
        mapPane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Line);
        // Get the list of stations in the shortest path
        List<Station> shortestPath = shortestRoute.getPath();
        // Draw a line between each pair of adjacent stations in the shortest path
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Station start = shortestPath.get(i);
            Station end = shortestPath.get(i + 1);

            drawLineBetweenStations(start, end, Color.MAGENTA);
        }
    }

    // This method draws a line between two stations with a given color
    private void drawLineBetweenStations(Station start, Station end, Color color) {
        // Calculate the actual coordinates of the start and end stations on the mapPane
        Point2D startPoint = calculateActualCoordinates(start);
        Point2D endPoint = calculateActualCoordinates(end);
        // Create a new line with the calculated start and end points and set its color and width
        javafx.scene.shape.Line line = new javafx.scene.shape.Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        line.setStroke(color);
        line.setStrokeWidth(4);
        // Add the line to the mapPane
        mapPane.getChildren().add(line);
    }








    // This method is called when the user wants to perform a breadth-first search to find the shortest path between two selected stations
    public void bfsSearch(ActionEvent actionEvent) {
        // Check if both start and end stations have been selected
        listView.getItems().clear();
        if (selectedStartStation == null || selectedEndStation == null) {
            listView.getItems().add("Please select both start and end stations");
            return;
        }
        // Find the shortest route between the selected start and end stations using the Graph's findShortestPath method
        Route shortestRoute = graph.findShortestPath(selectedStartStation, selectedEndStation);
        // If no path is found, print an error message
        if (shortestRoute == null) {
            listView.getItems().add("No path found between the selected stations");
        } else {
            // If a path is found, print the path and the number of stops
            List<Station> path = shortestRoute.getPath();
            listView.getItems().add(path.get(0).getStationName() + " to " + path.get(path.size() - 1).getStationName());
            listView.getItems().add("Shortest path:");
            for (int i = 0; i < path.size(); i++) {
                listView.getItems().add(path.get(i).getStationName());
                if (i < path.size() - 1) {
                    listView.getItems().add("↓");
                }
            }
            listView.getItems().add("Number of stops: " + shortestRoute.getStops() + "\n");
            drawShortestPath(shortestRoute);
        }
    }




    public void initialize(URL url, ResourceBundle resourceBundle){
    mainControl = this;
    }

    private class Stroke {
    }
}