package com.example.londonunderground.controller;

import com.example.londonunderground.models.Graph;
import com.example.londonunderground.models.Line;
import com.example.londonunderground.models.Route;
import com.example.londonunderground.models.Station;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class MainController implements Initializable {

    public static MainController mainControl;//Will be used to access the controller from other classes
    private final Graph graph = new Graph(); //Will be used to store the graph
    private final Map<String, Station> stations = new HashMap<>(); //Will be used to store all stations
    @FXML
    public ImageView zoneImage;
    @FXML
    public ListView routeOutput, waypointView, avoidView;
    @FXML
    public Button popMap, addWaypointButton, removeWaypointButton, addAvoidButton, removeAvoidButton;
    @FXML
    public Button clearMap;
    @FXML
    public Button exitApp;
    @FXML
    public MenuButton avoidStation;
    @FXML
    public MenuButton waypointStation;
    @FXML
    public MenuButton startStation;
    @FXML
    public MenuButton endStation;
    @FXML
    public AnchorPane mapPane;
    @FXML
    public Button bfsAlgorithm;
    @FXML
    public Button dijkstraAlgorithm;
    @FXML
    public Button dijkstrasWithLineCost;
    @FXML
    public Button depthFirstSearchButton;
    @FXML
    public TreeView depthFirstTree;
    @FXML
    public TextField numPermutations;


    private final List<Shape> stationShapes = new ArrayList<>();

    private List<javafx.scene.shape.Line> lineNodes = new ArrayList<>();
    private Station selectedStartStation;
    private Station selectedEndStation;
    private Circle startStationCircle;
    private Circle endStationCircle;
    private Circle startStationOuterRing;
    private Circle endStationOuterRing;
    private boolean isMapPopulated = false; // Boolean to check if the map is already populated



    public void populateMap(ActionEvent actionEvent) {
        // Check if the map is already populated
        if (isMapPopulated) {
            System.out.println("Map already populated");
            return;
        }

        // Read the CSV data from the file
        Path path = Paths.get("src/main/resources/CSV/zone1Corrected.csv");
        String csvData = "";
        try {
            csvData = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create lines and stations from the CSV data
        Map<String, Line> lines = createLinesAndStationsFromCSV(csvData);

        // Iterate over the lines and print their stations
        for (Map.Entry<String, Line> entry : lines.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().getColor());
            for (Station station : entry.getValue().getStations()) {
                System.out.println("\t" + station.getStationName() + " (" + station.getX() + "," + station.getY() + ")");
            }
            System.out.println("\n");
        }

        // Populate the menu buttons with the stations
        populateMenuButtons(lines);

        // Set the boolean to true after populating the map
        isMapPopulated = true;
    }



    private Map<String, Line> createLinesAndStationsFromCSV(String csvData) {
        Map<String, Line> lines = new HashMap<>();
        // Map<String, Station> stations = new HashMap<>();  // Remove this line
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

                // Print the distances
                System.out.println("Calculating distance between " + previousStation.getStationName() + " and " + stationObj.getStationName());
                System.out.println("Previous station coordinates: " + previousStation.getX() + ", " + previousStation.getY());
                System.out.println("Current station coordinates: " + stationObj.getX() + ", " + stationObj.getY());
                System.out.println("Calculated distance: " + distance);

                // Add the current station as a neighbor to the previous station, if they are not already neighbors
                if (!previousStation.getNeighbors().containsKey(stationObj)) {
                    previousStation.addNeighbor(stationObj);
                    System.out.println("Added " + stationObj.getStationName() + " as a neighbor to " + previousStation.getStationName());
                }

                // Add the previous station as a neighbor to the current station, if they are not already neighbors
                if (!stationObj.getNeighbors().containsKey(previousStation)) {
                    stationObj.addNeighbor(previousStation);
                    System.out.println("Added " + previousStation.getStationName() + " as a neighbor to " + stationObj.getStationName());
                }
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

        // Iterate through the list of stations
        for (Station station : stations) {
            // Create a new MenuItem with the station name
            MenuItem menuItem = new MenuItem(station.getStationName());

            // Set an action event handler for the MenuItem
            menuItem.setOnAction(e -> handleStationMenuItemClicked(e, station));

            // Add the MenuItem to the list of stationMenuItems
            stationMenuItems.add(menuItem);
        }

        // Return the list of stationMenuItems
        return stationMenuItems;
    }


    private void handleStationMenuItemClicked(ActionEvent event, Station station) {
        MenuItem clickedMenuItem = (MenuItem) event.getSource();
        MenuButton parentMenuButton = (MenuButton) clickedMenuItem.getParentPopup().getOwnerNode();

        // Set the selected station as the text of the parent MenuButton
        parentMenuButton.setText(station.getStationName());

        if (parentMenuButton == startStation) {
            selectedStartStation = station;
            drawStartStationCircle(station); // Draw a circle and outer ring around the selected start station
        } else if (parentMenuButton == endStation) {
            selectedEndStation = station;
            drawEndStationCircle(station); // Draw a circle and outer ring around the selected end station
        }
    }

    public void addWaypoint(ActionEvent actionEvent) {
        String newWaypoint = waypointStation.getText();

        if (waypointView.getItems().contains(newWaypoint)) {
            //station has already been added
            return;
        }
        waypointView.getItems().add(newWaypoint);
    }

    public void removeWaypoint(ActionEvent actionEvent) {
        String selectedWaypoint = (String) waypointView.getSelectionModel().getSelectedItem();
        waypointView.getItems().remove(selectedWaypoint);
    }

    public void addAvoid(ActionEvent actionEvent) {
        String newAvoid = avoidStation.getText();

        if (avoidView.getItems().contains(newAvoid)) {
            //station has already been added
            return;
        }
        avoidView.getItems().add(newAvoid);
    }

    public void removeAvoid(ActionEvent actionEvent) {
        String selectedAvoid = (String) avoidView.getSelectionModel().getSelectedItem();
        avoidView.getItems().remove(selectedAvoid);
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
        // Remove any existing lines and station shapes from the mapPane
        mapPane.getChildren().removeAll(lineNodes);
        mapPane.getChildren().removeAll(stationShapes);
        lineNodes.clear(); // Clear the list of line nodes
        stationShapes.clear(); // Clear the list of station shapes

        // Get the list of stations in the shortest path
        List<Station> shortestPath = shortestRoute.getPath();

        // Draw a circle at each station and a line to the next station
        for (int i = 0; i < shortestPath.size(); i++) {
            Station station = shortestPath.get(i);
            if (i != 0 && i != shortestPath.size() - 1) { // Skip start and end stations
                drawCircleAtStation(station, Color.BLACK, 5); // Draw a circle at the current station with a black color and radius of 5
            }
            // Draw a line to the next station, unless this is the last station in the path
            if (i < shortestPath.size() - 1) {
                Station nextStation = shortestPath.get(i + 1);
                javafx.scene.shape.Line line = drawLineBetweenStations(station, nextStation, Color.MAGENTA); // Draw a line between the current station and the next station with a magenta color
                lineNodes.add(line); // Add the line to the list of line nodes
            }
        }

        // Add the lines and shapes to the mapPane in the desired order
        mapPane.getChildren().removeAll(lineNodes);
        mapPane.getChildren().removeAll(stationShapes);
        mapPane.getChildren().addAll(lineNodes);
        mapPane.getChildren().addAll(stationShapes);
    }



    // This method draws a circle at a station
    private void drawCircleAtStation(Station station, Color color, double radius) {
        // Calculate the actual coordinates of the station on the mapPane
        Point2D actualCoordinates = calculateActualCoordinates(station);

        // Create a new circle with the calculated coordinates, specified color, and radius
        Circle circle = new Circle(actualCoordinates.getX(), actualCoordinates.getY(), radius);
        circle.setFill(color);

        // Add the circle to the list of station shapes
        stationShapes.add(circle);
    }


    // This method draws a line between two stations with a given color
    private javafx.scene.shape.Line drawLineBetweenStations(Station start, Station end, Color color) {
        // Calculate the actual coordinates of the start and end stations on the mapPane
        Point2D startPoint = calculateActualCoordinates(start);
        Point2D endPoint = calculateActualCoordinates(end);
        // Create a new line with the calculated start and end points and set its color and width
        javafx.scene.shape.Line line = new javafx.scene.shape.Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        line.setStroke(color);
        line.setStrokeWidth(4);
        // Add the line to the mapPane
        mapPane.getChildren().add(line);
        return line;
    }


    // This method is called when the user wants to perform a breadth-first search to find the shortest path between two selected stations
    public void bfsSearch(ActionEvent actionEvent) {
        // Check if both start and end stations have been selected
        if (selectedStartStation == null || selectedEndStation == null) {
            System.out.println("Please select both start and end stations");
            return;
        }

        // Find the shortest route between the selected start and end stations using the Graph's findShortestPath method
        Route shortestRoute = graph.findShortestPathBFS(selectedStartStation, selectedEndStation);

        // If no path is found, print an error message
        if (shortestRoute == null) {
            System.out.println("No path found between the selected stations");
        } else {
            // Print the start and end stations
            List<Station> path = shortestRoute.getPath();
            System.out.println(path.get(0).getStationName() + " to " + path.get(path.size() - 1).getStationName());

            // Initialize variables to keep track of the current line
            Line currentLine = null;
            Line nextLine = null;

            // Initialize lists to store the station path and line changes
            List<String> stationPath = new ArrayList<>();
            List<String> lineChanges = new ArrayList<>();

            // Initialize variable to keep track of the number of line changes
            int lineChangeCount = 0;

            // Iterate over each station in the path
            for (int i = 0; i < path.size() - 1; i++) {
                Station station1 = path.get(i);
                Station station2 = path.get(i + 1);

                // Get the common lines between the two stations
                List<Line> commonLines = getCommonLines(station1, station2);

                // Update the current line and line change information
                if (currentLine == null || !commonLines.contains(currentLine)) {
                    nextLine = commonLines.get(0);

                    // Increment the line change counter
                    if (i != 0) {
                        lineChangeCount++;
                    }

                    // Add the line change to the list
                    if (i != 0) {
                        lineChanges.add("Take " + currentLine.getLineName() + " to " + station1.getStationName());
                        lineChanges.add("Change to " + nextLine.getLineName());
                    } else {
                        lineChanges.add("Start with " + nextLine.getLineName());
                    }
                    currentLine = nextLine;
                }

                // Add the station to the station path list
                stationPath.add(station1.getStationName());
            }

            // Add the final station to the station path list
            stationPath.add(path.get(path.size() - 1).getStationName());

            // Add the final line change to the list
            lineChanges.add("Take " + currentLine.getLineName() + " to " + path.get(path.size()-1).getStationName());

            // Print out the station path
            System.out.println("Shortest path: ");
            System.out.println(String.join(" -> ", stationPath));

            // Print out the line changes
            System.out.println(String.join("\n", lineChanges));
            System.out.println("Number of stops: " + shortestRoute.getStops());
            System.out.println("Number of line changes: " + lineChangeCount);

            // Draw the shortest path on the map
            drawShortestPath(shortestRoute);

            // Update the ListView with the path details
            List<String> outputLines = new ArrayList<>();

            // Add the BFS header indicating the start and end stations
            outputLines.add("\nBFS: " + selectedStartStation.getStationName() + " to " + selectedEndStation.getStationName());
            outputLines.add("Number of stops: " + shortestRoute.getStops());
            outputLines.add("Number of line changes: " + (lineChangeCount));

            // Iterate over the stations in the path and add appropriate indicators
            for (int i = 0; i < path.size(); i++) {
                if (i == 0 || i == path.size() - 1) {
                    // Add a -- for the first and last stations
                    outputLines.add("-- " + path.get(i).getStationName() + " --");
                } else {
                    // Add a downward arrow marker for stations between start and finish
                    outputLines.add("↓ " + path.get(i).getStationName() + " ↓");
                }
            }

            // Add the line directions header and the list of line changes
            outputLines.add("\nLine Directions:");
            outputLines.addAll(lineChanges);

            // Create an observable list and populate it with the output lines
            ObservableList<String> items = FXCollections.observableArrayList(outputLines);
            routeOutput.setItems(items);

            // Customize the list cell appearance based on its index
            routeOutput.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                        if (getIndex() == 0) { // if this is the first item
                            // Apply bold and underline styling to the first item
                            setStyle("-fx-font-weight: bold; -fx-underline: true;");
                        } else {
                            // Apply normal styling to other items
                            setStyle("-fx-font-weight: normal; -fx-underline: false;");
                        }
                    }
                }
            });
        }
    }

    // This method is called when the user wants to perform a Dijkstra's search to find the shortest path between two selected stations
    public void dijkstraSearch(ActionEvent actionEvent) {
        // Check if both the start and end stations have been selected
        if (selectedStartStation == null || selectedEndStation == null) {
            System.out.println("Please select both start and end stations");
            return;
        }

        // Create a set of all stations
        Set<Station> allStations = new HashSet<>(stations.values());

        // Find the shortest route between the selected start and end stations using Dijkstra's algorithm
        Route shortestRoute = graph.findShortestPathDijkstra(allStations, selectedStartStation, selectedEndStation);

        // If no path is found, print an error message
        if (shortestRoute == null) {
            System.out.println("No path found between the selected stations");
        } else {
            // Print the start and end stations
            List<Station> path = shortestRoute.getPath();
            System.out.println(path.get(0).getStationName() + " to " + path.get(path.size() - 1).getStationName());

            // Initialize variables to keep track of the current line
            Line currentLine = null;
            Line nextLine = null;

            // Initialize lists to store the station path and line changes
            List<String> stationPath = new ArrayList<>();
            List<String> lineChanges = new ArrayList<>();
            int lineChangeCount = 0;

            // Iterate over each station in the path
            for (int i = 0; i < path.size() - 1; i++) {
                Station station1 = path.get(i);
                Station station2 = path.get(i + 1);

                // Get the common lines between the two stations
                List<Line> commonLines = getCommonLines(station1, station2);

                // Update the current line and line change information
                if (currentLine == null || !commonLines.contains(currentLine)) {
                    nextLine = commonLines.get(0);

                    // Increment the line change count
                    if (i != 0) {
                        lineChangeCount++;
                    }

                    // Add the line change to the list
                    if (i != 0) {
                        lineChanges.add("Take " + currentLine.getLineName() + " to " + station1.getStationName());
                        lineChanges.add("Change to " + nextLine.getLineName());
                    } else {
                        lineChanges.add("Start with " + nextLine.getLineName());
                    }

                    currentLine = nextLine;
                }

                // Add the station to the station path list
                stationPath.add(station1.getStationName());
            }

            // Add the final station to the station path list
            stationPath.add(path.get(path.size() - 1).getStationName());

            // Add the final line change to the list
            lineChanges.add("Take " + currentLine.getLineName() + " to " + path.get(path.size() - 1).getStationName());

            // Print out the station path
            System.out.println("Shortest path: ");
            System.out.println(String.join(" -> ", stationPath));

            // Print out the line changes
            System.out.println(String.join("\n", lineChanges));
            System.out.println("Distance: " + shortestRoute.getDistance());
            System.out.println("Number of stops: " + (path.size() - 1));
            System.out.println("Number of line changes: " + lineChangeCount);

            // Draw the shortest path on the map
            drawShortestPath(shortestRoute);

            // Updating the ListView
            List<String> outputLines = new ArrayList<>();

            // Add the Dijkstra header indicating the start and end stations and the distance
            outputLines.add("\nDijkstra: " + selectedStartStation.getStationName() + " to " + selectedEndStation.getStationName());
            double distance = shortestRoute.getDistance();
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String roundedDistance = decimalFormat.format(distance);
            outputLines.add("Distance: " + roundedDistance);
            outputLines.add("Stops: " + (path.size() - 1));
            outputLines.add("Line Changes: " + lineChangeCount);

            // Iterate over the stations in the path and add appropriate indicators
            for (int i = 0; i < path.size(); i++) {
                if (i == 0 || i == path.size() - 1) {
                    // Add a special marker for the first and last stations
                    outputLines.add("-- " + path.get(i).getStationName() + " --");
                } else {
                    // Add a downward arrow marker for intermediate stations
                    outputLines.add("↓ " + path.get(i).getStationName() + " ↓");
                }
            }

            // Add the line directions header and the list of line changes
            outputLines.add("\nLine Directions:");
            outputLines.addAll(lineChanges);

            // Create an observable list and populate it with the output lines
            ObservableList<String> items = FXCollections.observableArrayList(outputLines);
            routeOutput.setItems(items);

            // Customize the list cell appearance based on its index
            routeOutput.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                        if (getIndex() == 0) {
                            // Apply bold and underline styling to the first item
                            setStyle("-fx-font-weight: bold; -fx-underline: true;");
                        } else {
                            // Apply normal styling to other items
                            setStyle("-fx-font-weight: normal; -fx-underline: false;");
                        }
                    }
                }
            });
        }
    }

    public void dijkstraSearchWithPenalty(ActionEvent actionEvent) {
        // Check if both start and end stations have been selected
        if (selectedStartStation == null || selectedEndStation == null) {
            System.out.println("Please select both start and end stations");
            return;
        }

        // Create a set of all stations and set the line change penalty
        Set<Station> allStations = new HashSet<>(stations.values());
        double lineChangePenalty = 500;

        // Find the shortest route between the selected start and end stations using Dijkstra's algorithm with penalty
        Route shortestRoute = graph.findShortestPathDijkstraWithPenalty(allStations, selectedStartStation, selectedEndStation, lineChangePenalty);

        // If no path is found, print an error message
        if (shortestRoute == null) {
            System.out.println("No path found between the selected stations");
        } else {
            // Print the start and end stations
            List<Station> path = shortestRoute.getPath();
            System.out.println(path.get(0).getStationName() + " to " + path.get(path.size() - 1).getStationName());

            // Initialize variables to keep track of the current line
            Line currentLine = null;
            Line nextLine = null;

            // Initialize lists to store the station path and line changes
            List<String> stationPath = new ArrayList<>();
            List<String> lineChanges = new ArrayList<>();

            // Iterate over each station in the path
            for (int i = 0; i < path.size() - 1; i++) {
                Station station1 = path.get(i);
                Station station2 = path.get(i + 1);

                // Get the common lines between the two stations
                List<Line> commonLines = getCommonLines(station1, station2);

                // Update the current line and line change information
                if (currentLine == null || !commonLines.contains(currentLine)) {
                    nextLine = commonLines.get(0);

                    if (i != 0) {
                        lineChanges.add("Take " + currentLine.getLineName() + " to " + station1.getStationName());
                        lineChanges.add("Change to " + nextLine.getLineName());
                    } else {
                        lineChanges.add("Start with " + nextLine.getLineName());
                    }
                }

                // Update the current line for the next pair of stations
                if (i + 2 < path.size()) {
                    List<Line> nextCommonLines = getCommonLines(path.get(i + 1), path.get(i + 2));
                    if (nextCommonLines.contains(nextLine)) {
                        currentLine = nextLine;
                    }
                } else {
                    currentLine = nextLine;
                }

                // Add the station to the station path list
                stationPath.add(station1.getStationName());
            }

            // Add the final station to the station path list
            stationPath.add(path.get(path.size() - 1).getStationName());

            // Add the final line change to the list
            lineChanges.add("Take " + currentLine.getLineName() + " to " + path.get(path.size() - 1).getStationName());

            // Print the station path and line change information
            System.out.println("Shortest path: ");
            System.out.println(String.join(" -> ", stationPath));
            System.out.println(String.join("\n", lineChanges));
            System.out.println("Distance: " + shortestRoute.getDistance());
            System.out.println("Line changes: " + shortestRoute.getLineChanges());
            System.out.println("Line change penalty: " + shortestRoute.getLineChangePenalty());
            System.out.println("Stops: " + shortestRoute.getStops());

            // Draw the shortest path on the map
            drawShortestPath(shortestRoute);

            // Updating the ListView
            List<String> outputLines = new ArrayList<>();

            // Add the Dijkstra header indicating the start and end stations and the distance
            outputLines.add("\nLine Change Dijkstra: " + selectedStartStation.getStationName() + " to " + selectedEndStation.getStationName());
            double distance = shortestRoute.getDistance();
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String roundedDistance = decimalFormat.format(distance);
            outputLines.add("Distance: " + roundedDistance);
            outputLines.add("Line changes: " + shortestRoute.getLineChanges());
            outputLines.add("Line change penalty: " + shortestRoute.getLineChangePenalty());
            outputLines.add("Stops: " + shortestRoute.getStops());

            // Iterate over the stations in the path and add appropriate indicators
            for (int i = 0; i < path.size(); i++) {
                if (i == 0 || i == path.size() - 1) {
                    outputLines.add("-- " + path.get(i).getStationName() + " --");
                } else {
                    outputLines.add("↓ " + path.get(i).getStationName() + " ↓");
                }
            }

            // Add the line directions header and the list of line changes
            outputLines.add("\nLine Directions:");
            outputLines.addAll(lineChanges);

            // Create an observable list and populate it with the output lines
            ObservableList<String> items = FXCollections.observableArrayList(outputLines);
            routeOutput.setItems(items);

            // Customize the list cell appearance based on its index
            routeOutput.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                        if (getIndex() == 0) {
                            setStyle("-fx-font-weight: bold; -fx-underline: true;");
                        } else {
                            setStyle("-fx-font-weight: normal; -fx-underline: false;");
                        }
                    }
                }
            });
        }
    }

    public void depthFirstSearch(ActionEvent actionEvent) {
        // Get the selected start and end stations
        Station startStation = selectedStartStation;
        Station endStation = selectedEndStation;

        try {
            // Get the maximum number of paths from the numPermutations TextBox
            int maxPaths = Integer.parseInt(numPermutations.getText());

            // Find all paths between the start and end stations using Depth-First Search (DFS)
            List<Route> paths = graph.findAllPathsDFS(startStation, endStation, maxPaths);

            // Populate the tree view with the found paths
            populateTreeView(depthFirstTree, paths);

            // Add a listener to the selection model of the tree view
            MultipleSelectionModel<TreeItem<String>> selectionModel = depthFirstTree.getSelectionModel();
            selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue instanceof RouteTreeItem) {
                    Route selectedRoute = ((RouteTreeItem) newValue).getRoute();
                    updateListViewWithRouteDetails(selectedRoute);
                }
            });
        } catch (NumberFormatException e) {
            // Show an alert dialog to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid integer in the numPermutations field.");
            alert.showAndWait();
        }
    }

    // Populate the list view with the selected DFS route details
    private void updateListViewWithRouteDetails(Route selectedRoute) {
        List<Station> path = selectedRoute.getPath();
        Line currentLine = null;
        Line nextLine = null;
        List<String> stationPath = new ArrayList<>();
        List<String> lineChanges = new ArrayList<>();
        int lineChangeCount = 0;

        // Iterate over the path to determine station path and line changes
        for (int i = 0; i < path.size() - 1; i++) {
            Station station1 = path.get(i);
            Station station2 = path.get(i + 1);
            List<Line> commonLines = getCommonLines(station1, station2);

            if (currentLine == null || !commonLines.contains(currentLine)) {
                nextLine = commonLines.get(0);
                if (i != 0) {
                    lineChangeCount++;
                }

                if (i != 0) {
                    lineChanges.add("Take " + currentLine.getLineName() + " to " + station1.getStationName());
                    lineChanges.add("Change to " + nextLine.getLineName());
                } else {
                    lineChanges.add("Start with " + nextLine.getLineName());
                }
                currentLine = nextLine;
            }
            stationPath.add(station1.getStationName());
        }

        // Add the last station in the path
        stationPath.add(path.get(path.size() - 1).getStationName());
        lineChanges.add("Take " + currentLine.getLineName() + " to " + path.get(path.size() - 1).getStationName());

        // Prepare the output lines for the ListView
        List<String> outputLines = new ArrayList<>();
        outputLines.add("\nDFS: " + path.get(0).getStationName() + " to " + path.get(path.size() - 1).getStationName());
        outputLines.add("Number of stops: " + selectedRoute.getStops());
        outputLines.add("Number of line changes: " + lineChangeCount);

        for (int i = 0; i < path.size(); i++) {
            if (i == 0 || i == path.size() - 1) {
                outputLines.add("-- " + path.get(i).getStationName() + " --");
            } else {
                outputLines.add("↓ " + path.get(i).getStationName() + " ↓");
            }
        }

        outputLines.add("\nLine Directions:");
        outputLines.addAll(lineChanges);

        // Update the ListView with the output lines
        ObservableList<String> items = FXCollections.observableArrayList(outputLines);
        routeOutput.setItems(items);

        // Customize the ListView cells to highlight certain lines
        routeOutput.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    if (getIndex() == 0) {
                        setStyle("-fx-font-weight: bold; -fx-underline: true;");
                    } else {
                        setStyle("-fx-font-weight: normal; -fx-underline: false;");
                    }
                }
            }
        });
    }

    public void populateTreeView(TreeView<String> treeView, List<Route> paths) {
        // Create root node
        TreeItem<String> rootItem = new TreeItem<>("All Route Permutations:");
        rootItem.setExpanded(true);

        // Iterate over each path
        for (int i = 0; i < paths.size(); i++) {
            Route path = paths.get(i);
            TreeItem<String> pathItem = new RouteTreeItem("Route - " + (i + 1), path);
            List<Station> stations = path.getPath();

            // Iterate over each station in path
            for (int j = 0; j < stations.size(); j++) {
                String stationName = stations.get(j).getStationName();
                String stationNameFormatted = stationName;

                // Format station name with arrows or dashes
                if (j == 0 || j == stations.size() - 1) {
                    stationNameFormatted = "-- " + stationName + " --";
                } else {
                    stationNameFormatted = "↓ " + stationName + " ↓";
                }

                // Create tree item for station and add it to the path item
                TreeItem<String> stationItem = new TreeItem<>(stationNameFormatted);
                pathItem.getChildren().add(stationItem);
            }

            // Add path item to the root item
            rootItem.getChildren().add(pathItem);
        }

        // Add root item to the tree view
        treeView.setRoot(rootItem);
    }



    public class RouteTreeItem extends TreeItem<String> {
        private Route route;

        //Constructs a RouteTreeItem with a given name and associated Route.
        public RouteTreeItem(String name, Route route) {
            super(name);
            this.route = route;
        }

        //Returns the Route associated with this RouteTreeItem.
        public Route getRoute() {
            return route;
        }
    }

    public List<Line> getCommonLines(Station station1, Station station2) {
        // Create a new list and initialize it with the lines of the first station
        List<Line> commonLines = new ArrayList<>(station1.getLines());

        // Retain only the lines that are also present in the lines of the second station
        commonLines.retainAll(station2.getLines());

        // Return the list of common lines
        return commonLines;
    }

    public void handleMapMouseClicked(MouseEvent event) {
        // I want to modify this method so that it only works if isMapPopulated is true and print a message to please populate the map if it is false
        if (!isMapPopulated) {
            System.out.println("Please populate the map first");
            return;
        }

        // Get the scale of the image
        double scaleX = zoneImage.getImage().getWidth() / zoneImage.getFitWidth();
        double scaleY = zoneImage.getImage().getHeight() / zoneImage.getFitHeight();

        // Adjust the event coordinates to the scale of the image
        double x = event.getX() * scaleX;
        double y = event.getY() * scaleY;

        // If left button is clicked, set the start station
        if (event.getButton() == MouseButton.PRIMARY) {
            selectedStartStation = findNearestStation(x, y);
            drawStartStationCircle(selectedStartStation);
            selectMenuItem(startStation, selectedStartStation.getStationName());

            // Print the adjacent stations
            printAdjacentStations(selectedStartStation);
        }
        // If right button is clicked, set the end station
        else if (event.getButton() == MouseButton.SECONDARY) {
            selectedEndStation = findNearestStation(x, y);
            drawEndStationCircle(selectedEndStation);
            selectMenuItem(endStation, selectedEndStation.getStationName());

            // Print the adjacent stations
            printAdjacentStations(selectedStartStation);
        }
    }

    //Print adjacent stations is used for debugging
    private void printAdjacentStations(Station station) {// Print out the adjacent stations of the given station
        System.out.println("Adjacent stations to " + station.getStationName() + ":");// Print out the station name
        for (Station adjacentStation : station.getNeighbors().keySet()) {// Iterate over the adjacent stations
            System.out.println(adjacentStation.getStationName());// Print out the name of the adjacent station
        }
    }

    private Station findNearestStation(double x, double y) {
        // Initialize variables to store the nearest station and its distance
        Station nearestStation = null;
        double nearestDistance = Double.MAX_VALUE;

        // Iterate over all stations to find the nearest one
        for (Station station : stations.values()) {
            // Calculate the distance between the given coordinates and the station's coordinates
            double distance = calculateDistance(x, y, station.getX(), station.getY());

            // If the calculated distance is smaller than the current nearest distance,
            // update the nearest station and nearest distance
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestStation = station;
            }
        }

        // Return the nearest station
        return nearestStation;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        // Calculate the distance between two points using the Euclidean distance formula
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void selectMenuItem(MenuButton menuButton, String stationName) {
        // Iterate over the items in the menuButton's list and select the item with matching stationName
        for (MenuItem item : menuButton.getItems()) {
            if (item.getText().equals(stationName)) {
                // Set the menuButton's text to the selected item's text
                menuButton.setText(item.getText());
                break;
            }
        }
    }

    public void clearMap(ActionEvent actionEvent) {
        // Clear the lines from the map
        mapPane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Line);

        // Clear the black circles from the map
        mapPane.getChildren().removeIf(node -> node instanceof Circle && ((Circle) node).getFill().equals(Color.BLACK));

        // Also clear the listview
        routeOutput.getItems().clear();

        // Clear the tree view
        depthFirstTree.setRoot(null);
    }

    public void closeApplication(ActionEvent actionEvent) {
        //close the application
        Platform.exit();
    }

    public void initialize(URL url, ResourceBundle resourceBundle){
        mainControl = this; // Set the mainControl to this instance of the controller

        // Set the menuButton's text to the first item's text
        zoneImage.setOnMouseClicked(mouseEvent -> {
            // Get the x and y coordinates of the click
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();

            // Find the nearest station
            Station nearestStation = findNearestStation(x, y);

            // If left mouse button was clicked, set the start station
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                selectedStartStation = nearestStation;
            }
            // If right mouse button was clicked, set the end station
            else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                selectedEndStation = nearestStation;
            }
        });

        // Set the mouse click event handler for the map
        zoneImage.setOnMouseClicked(this::handleMapMouseClicked);

        depthFirstTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Check if a path was selected
            if (newValue != null && newValue instanceof RouteTreeItem) {
                // Get the selected path
                Route selectedPath = ((RouteTreeItem) newValue).getRoute();
                // Draw the path
                drawShortestPath(selectedPath);
            }
        });
    }
}