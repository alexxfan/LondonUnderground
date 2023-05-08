package com.example.londonunderground.controller;

import com.example.londonunderground.models.Line;
import com.example.londonunderground.models.Station;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainController implements Initializable {

    public static MainController mainControl;
    @FXML
    public ImageView zoneImage;
    @FXML
    public Button popMap;
    @FXML
    public Button clearMap;
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

        // Possible further actions to go here
        if (parentMenuButton == startStation) {
            drawCircleOnMap(station); //station location test
        }
    }



    private Point2D calculateActualCoordinates(Station station) {
        double scaleX = zoneImage.getBoundsInLocal().getWidth() / zoneImage.getImage().getWidth();
        double scaleY = zoneImage.getBoundsInLocal().getHeight() / zoneImage.getImage().getHeight();

        double actualX = station.getX() * scaleX;
        double actualY = station.getY() * scaleY;

        return new Point2D(actualX, actualY);
    }




    private void drawCircleOnMap(Station station) {
        // Remove any existing circles
        mapPane.getChildren().removeIf(node -> node instanceof Circle);

        // Adjust these values as needed to align the circle correctly
        double offsetX = 5; // Adjust this value
        double offsetY = 1; // Adjust this value

        // Calculate the actual x and y coordinates on the ImageView
        Point2D actualCoordinates = calculateActualCoordinates(station);

        // Create a new circle with desired properties
        Circle circle = new Circle(actualCoordinates.getX(), actualCoordinates.getY(), 5, Color.RED);

        // Add the circle to the mapPane
        mapPane.getChildren().add(circle);
    }




    public void initialize(URL url, ResourceBundle resourceBundle){
    mainControl = this;
    }
}