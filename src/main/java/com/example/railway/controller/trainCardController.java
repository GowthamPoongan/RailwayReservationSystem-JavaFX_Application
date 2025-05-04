package com.example.railway.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class trainCardController {
    @FXML private Label trainName, timing, fare, route;
    @FXML private Button bookButton;

    private Runnable bookingCallback;

    public void setTrainData(String name, String time, String fareValue, String routeText) {
        trainName.setText(name);
        timing.setText("Time: " + time);
        fare.setText("Fare: " + fareValue);
        route.setText("Route: " + routeText);
    }

    public void setBookingCallback(Runnable callback) {
        this.bookingCallback = callback;
        bookButton.setOnAction(e -> {
            if (bookingCallback != null) bookingCallback.run();
        });
    }
}
