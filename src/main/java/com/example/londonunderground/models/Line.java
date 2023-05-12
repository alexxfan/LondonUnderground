package com.example.londonunderground.models;

import java.util.ArrayList;
import java.util.List;

public class Line {
    // The name of the line
    private String lineName;
    // The color of the line
    private String color;
    // A list of stations on the line
    private List<Station> stations;

    public Line(String lineName, String color) {
        // Initialize the line with a name and color
        this.lineName = lineName;
        this.color = color;
        // Initialize the list of stations as an empty ArrayList
        this.stations = new ArrayList<>();
    }

    public String getLineName() {
        // Get the name of the line
        return lineName;
    }

    public String getColor() {
        // Get the color of the line
        return color;
    }

    public List<Station> getStations() {
        // Get the list of stations on the line
        return stations;
    }

    public void addStation(Station station) {
        // Add a station to the line by adding it to the list of stations
        this.stations.add(station);
        station.addLine(this);// add this line to the station
    }


}
