package com.example.railway.controller;

import com.example.railway.model.Traveller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private String trainName;
    private String fromLocation;
    private String toLocation;
    private LocalDate travelDate;
    private String departureTime;
    private String arrivalTime;
    private double fare;
    private String travelClass;
    private List<Traveller> travellers = new ArrayList<>();

    public Booking(String trainName, String fromLocation, String toLocation, LocalDate travelDate,
                   String departureTime, String arrivalTime, double fare, String travelClass) {
        this.trainName = trainName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.travelDate = travelDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.travelClass = travelClass;
    }

    // ✅ Getters
    public String getTrainName() { return trainName; }
    public LocalDate getTravelDate() { return travelDate; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public double getFare() { return fare; }
    public String getTravelClass() { return travelClass; }
    public List<Traveller> getTravellers() { return travellers; }

    public void addTraveller(Traveller traveller) {
        this.travellers.add(traveller);
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public String getBookingDateString() {
        return travelDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    // Optional: add bookingId later if needed
    public String getBookingId() {
        return null;
    }
}
