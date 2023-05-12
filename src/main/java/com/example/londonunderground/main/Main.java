package com.example.londonunderground.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.londonunderground.controller.MainController.mainControl;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/londonunderground/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        Image icon = new Image("data:image/png;base64,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");
        stage.getIcons().add(icon);
        stage.setTitle("London Underground Path Finder");
        stage.setScene(scene);

        // Load the image and set it to the ImageView with fx:id zoneImage
        Path imagePath = Paths.get("src/main/resources/Image/Zone1Alt.png");
        InputStream inputStream = Files.newInputStream(imagePath);
        Image image = new Image(inputStream);
        mainControl.zoneImage.setPreserveRatio(true);
        mainControl.zoneImage.setImage(image);
        double width = image.getWidth();
        double height = image.getHeight();
        double fitWidth = mainControl.zoneImage.getFitWidth();
        double fitHeight = mainControl.zoneImage.getFitHeight();
        System.out.println("Image dimensions: " + width + " x " + height + " pixels");
        System.out.println("ImageView dimensions: " + fitWidth + " x " + fitHeight + " pixels");

        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}