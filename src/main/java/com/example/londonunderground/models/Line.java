package com.example.londonunderground.models;

import java.util.ArrayList;
import java.util.List;

public class Line {
    // FIELDS
    private String lineName;
    private String color;
    private List<Station> stations;

    // CONSTRUCTOR
    public Line(String lineName, String color) {
        this.lineName = lineName;
        this.color = color;
        this.stations = new ArrayList<>();
    }

    // GETTERS
    public String getLineName() {
        return lineName;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }

    // METHOD

    public void addStation(Station station) {
        // Add a station to the line by adding it to the list of stations
        this.stations.add(station);
        station.addLine(this);// add this line to the station
    }


}
