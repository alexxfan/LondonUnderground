package com.example.londonunderground.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public static MainController mainControl;
    @FXML
    public ImageView zoneImage;

    public void initialize(URL url, ResourceBundle resourceBundle){
    mainControl = this;
    }
}