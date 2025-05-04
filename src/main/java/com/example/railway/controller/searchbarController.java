package com.example.railway.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class searchbarController {

    @FXML
    private ComboBox<String> fromComboBox;

    @FXML
    private ComboBox<String> toComboBox;

    @FXML
    private ComboBox<String> classComboBox;

    @FXML
    private DatePicker travelDate;

    @FXML
    private Label fromStationLabel;

    @FXML
    private Label toStationLabel;

    @FXML
    private Button searchButton;

    private final String[] stations = {
            "MAS, Chennai Central Railway Station",
            "NDLS, New Delhi Railway Station",
            "CSTM, Mumbai CST",
            "HWH, Howrah Junction",
            "SBC, Bangalore City Junction"
    };

    @FXML
    public void initialize() {
        fromComboBox.getItems().addAll(stations);
        toComboBox.getItems().addAll(stations);
        classComboBox.getItems().addAll("ALL", "Sleeper", "AC 3-Tier", "AC 2-Tier", "First Class");

        fromComboBox.setValue(stations[0]);
        toComboBox.setValue(stations[1]);
        classComboBox.setValue("ALL");

        fromStationLabel.setText(stations[0]);
        toStationLabel.setText(stations[1]);

        fromComboBox.setOnAction(e -> fromStationLabel.setText(fromComboBox.getValue()));
        toComboBox.setOnAction(e -> toStationLabel.setText(toComboBox.getValue()));

        searchButton.setOnMouseClicked(this::handleSearch);
    }

    private void handleSearch(MouseEvent event) {
        String from = fromComboBox.getValue();
        String to = toComboBox.getValue();
        String travelClass = classComboBox.getValue();
        String date = (travelDate.getValue() != null) ? travelDate.getValue().toString() : null;

        if (from == null || to == null || date == null || travelClass == null) {
            showAlert("Please fill in all fields before searching.");
            return;
        }

        if (from.equals(to)) {
            showAlert("Departure and arrival stations cannot be the same.");
            return;
        }

        System.out.println("Searching from " + from + " to " + to + " on " + date + " in class: " + travelClass);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private FlowPane trainResultsPane; // Bind this to where train cards will appear

    @FXML
    private void onSearchClicked() {
        // Clear old results
        trainResultsPane.getChildren().clear();

        // Create dummy train cards
        for (int i = 1; i <= 3; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/railway/views/traincard.fxml"));
                Parent card = loader.load();
                trainCardController controller = loader.getController();

                controller.setTrainData(
                        "Tamil Nadu SF Exp #" + (12620 + i),
                        "10:00 PM, 17 APR",
                        "₹" + (800 + i * 300),
                        "Mgr Chennai Ctl ➝ New Delhi"
                );

                int finalI = i;
                controller.setBookingCallback(() -> {
                    showBookingPopup();
                    MyBookingsData.addBooking("Tamil Nadu SF Exp #" + (12620 + finalI));
                });

                trainResultsPane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void showBookingPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Confirmed");
        alert.setHeaderText(null);
        alert.setContentText("🎉 Your ticket was booked!");
        alert.getDialogPane().setStyle("-fx-background-radius: 15; -fx-font-size: 14;");
        alert.showAndWait();
    }




}
