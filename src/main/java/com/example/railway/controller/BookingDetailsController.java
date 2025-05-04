package com.example.railway.controller;

import com.example.railway.database.DatabaseConnection;
import com.example.railway.database.LoggedInUser;
import com.example.railway.model.Train;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDetailsController {

    @FXML private VBox travellerContainer;
    @FXML private Label trainNameLabel, fromToLabel, classLabel, fareLabel, totalFareLabel;
    @FXML private DatePicker travelDatePicker;
    @FXML private Button addTravellerBtn, payAndBookBtn;
    @FXML private ImageView trainImage;

    private Train selectedTrain;
    private final List<TravellerInput> travellerInputs = new ArrayList<>();

    public void setBookingData(Train train, LocalDate travelDate) {
        this.selectedTrain = train;

        trainNameLabel.setText(train.getName());
        fromToLabel.setText(train.getFromLocation() + " → " + train.getToLocation());
        classLabel.setText("Class: " + train.getTravelClass());
        fareLabel.setText("Fare per person: ₹" + train.getFare());
        URL imageUrl = getClass().getResource("/com/example/railway/images/DC_banner.jpg");
        if (imageUrl != null) {
            trainImage.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.err.println("❌ train.jpg not found!");
        }

        travelDatePicker.setValue(travelDate);
        addTravellerInput();
        updateTotalFare();
    }

    @FXML
    private void addTravellerInput() {
        HBox inputRow = new HBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setPrefWidth(160);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        phoneField.setPrefWidth(140);

        Button deleteBtn = new Button("✖");
        deleteBtn.setOnAction(e -> {
            travellerContainer.getChildren().remove(inputRow);
            travellerInputs.removeIf(input -> input.row == inputRow);
            updateTotalFare();
        });

        inputRow.getChildren().addAll(nameField, phoneField, deleteBtn);
        travellerContainer.getChildren().add(inputRow);

        travellerInputs.add(new TravellerInput(inputRow, nameField, phoneField));
        updateTotalFare();
    }

    private void updateTotalFare() {
        double fare = selectedTrain.getFare();
        int numTravellers = travellerInputs.size();
        double total = fare * numTravellers;
        totalFareLabel.setText("₹" + total);
    }

    @FXML
    private void handlePayAndBook() {
        if (selectedTrain == null || travelDatePicker.getValue() == null) {
            showAlert("Missing data", "Train or travel date not selected");
            return;
        }

        String email = LoggedInUser.getEmail();
        if (email == null || email.isEmpty()) {
            showAlert("Error", "User not logged in");
            return;
        }

        if (travellerInputs.isEmpty()) {
            showAlert("Add Travellers", "Please add at least one traveller");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String bookingSql = "INSERT INTO bookings (user_email, train_name, from_station, to_station, travel_date, departure_time, arrival_time, fare, class) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement bookingStmt = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            bookingStmt.setString(1, email);
            bookingStmt.setString(2, selectedTrain.getName());
            bookingStmt.setString(3, selectedTrain.getFromLocation().split(",")[0]);
            bookingStmt.setString(4, selectedTrain.getToLocation().split(",")[0]);
            bookingStmt.setDate(5, java.sql.Date.valueOf(travelDatePicker.getValue()));
            bookingStmt.setString(6, selectedTrain.getDepartureTime());
            bookingStmt.setString(7, selectedTrain.getArrivalTime());
            bookingStmt.setDouble(8, selectedTrain.getFare() * travellerInputs.size());
            bookingStmt.setString(9, selectedTrain.getTravelClass());

            bookingStmt.executeUpdate();

            ResultSet keys = bookingStmt.getGeneratedKeys();
            int bookingId = -1;
            if (keys.next()) {
                bookingId = keys.getInt(1);
            }

            if (bookingId == -1) throw new Exception("Failed to get booking ID");

            String travellerSql = "INSERT INTO travellers (booking_id, name, phone) VALUES (?, ?, ?)";
            PreparedStatement travellerStmt = conn.prepareStatement(travellerSql);

            for (TravellerInput input : travellerInputs) {
                travellerStmt.setInt(1, bookingId);
                travellerStmt.setString(2, input.nameField.getText());
                travellerStmt.setString(3, input.phoneField.getText());
                travellerStmt.addBatch();
            }

            travellerStmt.executeBatch();
            conn.commit();

            // 🎉 Show confirmation and load MyBookings after delay
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Booking Confirmed");
            alert.setContentText("🎉 Ticket booked successfully! Redirecting to My Bookings...");
            alert.show();

            // ⏳ Delay 3 seconds then redirect to My Bookings
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // 3 seconds
                    javafx.application.Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/railway/views/mybookings.fxml"));
                            Parent bookingsView = loader.load();

                            MyBookingsController controller = loader.getController();
                            controller.loadBookingsForUser(email);

                            // Set in the mainPane (use static mainPane reference)
                            dashboardController.getMainPane().setCenter(bookingsView);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert("Error", "Could not load My Bookings.");
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save booking: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleAddTraveller(ActionEvent actionEvent) {
        addTravellerInput();
    }

    private static class TravellerInput {
        HBox row;
        TextField nameField;
        TextField phoneField;

        public TravellerInput(HBox row, TextField nameField, TextField phoneField) {
            this.row = row;
            this.nameField = nameField;
            this.phoneField = phoneField;
        }
    }
}
