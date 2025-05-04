package com.example.railway.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
// Import the shared Booking data model class
import com.example.railway.controller.Booking; // Make sure this import is correct based on your package

public class BookingItemController {

    @FXML
    private Label bookingIdLabel;

    @FXML
    private Label nameLabel; // Maybe rename to trainNameLabel?

    @FXML
    private Label roomLabel; // This name seems inconsistent with a train booking. Rename to routeLabel?

    @FXML
    private Label checkInLabel; // This name seems inconsistent with a train booking. Rename to dateLabel?

    @FXML
    private Label checkOutLabel; // This name seems inconsistent with a train booking. Rename to classLabel?

    @FXML
    private Label guestLabel; // This name seems inconsistent with a train booking. Rename to fareLabel?

    @FXML
    private Label statusLabel; // Status of the booking

    // Correct the method signature to use the shared Booking model
    public void setBooking(Booking booking) { // Use the shared Booking class
        if (bookingIdLabel != null) bookingIdLabel.setText("Booking ID: " + booking.getBookingId());
        if (nameLabel != null) nameLabel.setText("Train: " + booking.getTrainName());
        // Adjust based on your actual Booking model fields and desired display
        if (roomLabel != null) roomLabel.setText("Route: " + booking.getFromLocation() + " to " + booking.getToLocation()); // Using From/To Location from shared Booking
        if (checkInLabel != null) checkInLabel.setText("Date: " + booking.getBookingDateString()); // Using the formatted date string
        if (checkOutLabel != null) checkOutLabel.setText("Class: " + booking.getTravelClass()); // Using travel class
        if (guestLabel != null) guestLabel.setText("Fare: ₹" + String.format("%.2f", booking.getFare())); // Using fare
        if (statusLabel != null) statusLabel.setText("Status: Confirmed"); // Replace with actual status if stored

        // You might also want to display departure/arrival times if your Booking model includes them
        // if (departureTimeLabel != null) departureTimeLabel.setText("Dep: " + booking.getDepartureTime());
        // if (arrivalTimeLabel != null) arrivalTimeLabel.setText("Arr: " + booking.getArrivalTime());
    }
}