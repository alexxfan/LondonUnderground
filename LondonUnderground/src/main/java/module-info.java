module com.example.londonunderground {
    requires javafx.controls;
    requires javafx.fxml;


    exports com.example.londonunderground.main;
    opens com.example.londonunderground to javafx.fxml;
    exports com.example.londonunderground.controller;
    opens com.example.londonunderground.controller to javafx.fxml;
}