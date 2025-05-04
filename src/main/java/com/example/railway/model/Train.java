package com.example.railway.model; // Make sure the package is correct

public class Train {
    private String name;
    private String departureTime;
    private String arrivalTime;
    private double fare;
    private String travelClass;
    private String fromLocation;
    private String toLocation;

    public Train() {
        this.name = name;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.travelClass = travelClass;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
    }

    // Getters
    public String getName() { return name; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public double getFare() { return fare; }
    public String getTravelClass() { return travelClass; }
    public String getFromLocation() { return fromLocation; }
    public String getToLocation() { return toLocation; }

    // You might add setters if needed
}