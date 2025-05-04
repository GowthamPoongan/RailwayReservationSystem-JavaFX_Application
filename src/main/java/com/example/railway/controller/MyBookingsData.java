package com.example.railway.controller;

import com.example.railway.controller.Booking;
import com.example.railway.model.Traveller;
import com.example.railway.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class MyBookingsData {

    public static List<Booking> getBookingsForUser(String email) {
        List<Booking> bookings = new ArrayList<>();

        String bookingSql = "SELECT b.id AS booking_id, b.train_name, b.from_station, b.to_station, " +
                "b.travel_date, b.departure_time, b.arrival_time, b.fare, b.class, t.name AS traveller_name, t.phone " +
                "FROM bookings b " +
                "LEFT JOIN travellers t ON b.id = t.booking_id " +
                "WHERE b.user_email = ? ORDER BY b.booked_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(bookingSql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            Map<Integer, Booking> bookingMap = new LinkedHashMap<>();

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");

                Booking booking = bookingMap.getOrDefault(bookingId, new Booking(
                        rs.getString("train_name"),
                        rs.getString("from_station"),
                        rs.getString("to_station"),
                        rs.getDate("travel_date").toLocalDate(),
                        rs.getString("departure_time"),
                        rs.getString("arrival_time"),
                        rs.getDouble("fare"),
                        rs.getString("class")
                ));

                String travellerName = rs.getString("traveller_name");
                String travellerPhone = rs.getString("phone");
                if (travellerName != null && travellerPhone != null) {
                    booking.addTraveller(new Traveller(travellerName, travellerPhone));
                }

                bookingMap.put(bookingId, booking);
            }

            bookings.addAll(bookingMap.values());

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Database error fetching bookings for user: " + email);
        }

        return bookings;
    }

    public static void addBooking(String s) {

    }
}
