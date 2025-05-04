// Updated MyBookingsController.java
package com.example.railway.controller;

import com.example.railway.database.LoggedInUser;
import com.example.railway.model.Traveller;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.List;

public class MyBookingsController {

    @FXML
    private VBox bookingsContainer;

    private List<Booking> userBookings;

    public void loadBookingsForUser(String userEmail) {
        bookingsContainer.getChildren().clear();

        List<Booking> bookings = MyBookingsData.getBookingsForUser(userEmail);
        userBookings = bookings;

        if (bookings.isEmpty()) {
            Label noBookingsLabel = new Label("No bookings found for this user.");
            bookingsContainer.getChildren().add(noBookingsLabel);
        } else {
            for (Booking booking : bookings) {
                VBox bookingCard = createBookingCard(booking);
                bookingsContainer.getChildren().add(bookingCard);
            }
        }
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(5);
        card.getStyleClass().add("booking-card");
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.2, 0, 4);");

        Label trainNameLabel = new Label("🚆 " + booking.getTrainName());
        trainNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2979ff;");

        Label routeLabel = new Label("From: " + booking.getFromLocation() + " → To: " + booking.getToLocation());
        Label dateLabel = new Label("Date: " + booking.getBookingDateString());
        Label classLabel = new Label("Class: " + booking.getTravelClass());
        Label fareLabel = new Label("Fare: ₹" + String.format("%.2f", booking.getFare()));

        VBox travellerBox = new VBox(3);
        travellerBox.setPadding(new Insets(5));
        travellerBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

        for (Traveller traveller : booking.getTravellers()) {
            Label travellerLabel = new Label("👤 " + traveller.getName() + " (" + traveller.getPhone() + ")");
            travellerBox.getChildren().add(travellerLabel);
        }

        card.getChildren().addAll(trainNameLabel, routeLabel, dateLabel, classLabel, fareLabel, travellerBox);
        return card;
    }
}
