package com.example.londonunderground.controller;

import com.example.londonunderground.models.GraphNode;
import com.example.londonunderground.models.Station;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class API {

    private List<Station> stationList;
    private HashMap<String, GraphNode<Station>> stationMap;
    private List<String> waypointList;
    private List<GraphNode<Station>> avoidedStation;
    private Image zone1Image;
    private Image breadthFirstSearchImage;



    public API(){
        this.zone1Image = new Image(getClass().getResourceAsStream("/Image/Zone1Alt.png"));
        this.stationList = new LinkedList<>();
        this.stationMap = new HashMap<>();
        this.waypointList = new LinkedList<>();
    }


}

