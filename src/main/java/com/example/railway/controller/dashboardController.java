package com.example.railway.controller;

import com.example.railway.database.DatabaseConnection; // Import DatabaseConnection
import com.example.railway.database.LoggedInUser; // Import LoggedInUser (assuming in util package)
import com.example.railway.model.Train; // Make sure this import is present
import com.example.railway.controller.Booking; // Import Booking model if needed here (likely not anymore)
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
// import com.example.railway.controller.MyBookingsData; // Remove if not used here

import java.io.IOException;
import java.net.URL;
import java.sql.Connection; // Added SQL imports
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Import SQLException
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // Import for Set, HashSet, List, ArrayList

public class dashboardController {
    @FXML private ComboBox<String> fromCombo;
    @FXML private ComboBox<String> toCombo;
    @FXML private ComboBox<String> classCombo;
    @FXML private DatePicker datePicker;
    @FXML private FlowPane summaryBox;
    @FXML private Label badgeLabel; // Keep if badge is in dashboard.fxml
    @FXML private BorderPane mainPane;
    public static dashboardController instance;
    private static BorderPane staticMainPane;

    // @FXML private VBox centerContent; // Remove if not used after merging
    @FXML private ScrollPane resultsScrollPane; // Keep if search results displayed here
    @FXML private VBox resultsContainer; // Keep if search results displayed here

    @FXML private Label welcomeLabel; // Added fx:id for the welcome label

    @FXML
    private void handleLogout() {
        try {
            // Clear the logged-in user
            LoggedInUser.clear(); // If you have a clear() method, or set email to null

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/railway/views/login.fxml"));
            Parent loginRoot = loader.load();

            // Show it in the same window
            Stage stage = (Stage) mainPane.getScene().getWindow(); // or any node from the scene
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to logout.");
        }
    }



    // Inner class to represent Train data (kept here as it's used for search results display)
    public static class Train extends com.example.railway.model.Train {
        private String name;
        private String departureTime;
        private String arrivalTime;
        private double fare;
        private String travelClass;
        private String fromLocation;
        private String toLocation;

        public Train(String name, String departureTime, String arrivalTime, double fare, String travelClass, String fromLocation, String toLocation) {
            super();
            this.name = name;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.fare = fare;
            this.travelClass = travelClass;
            this.fromLocation = fromLocation;
            this.toLocation = toLocation;
        }

        public String getName() { return name; }
        public String getDepartureTime() { return departureTime; }
        public String getArrivalTime() { return arrivalTime; }
        public double getFare() { return fare; }
        public String getTravelClass() { return travelClass; }
        public String getFromLocation() { return fromLocation; }
        public String getToLocation() { return toLocation; }
    }


    public void initialize() {
        staticMainPane = mainPane;
        instance = this;
        System.out.println("✅ mainPane injected: " + (mainPane != null));
        // Initialization for elements in dashboard.fxml that are not in the center content
        // For elements in the center content (like search bar), their initialization happens when the FXML is loaded.

        loadStations(); // Load data into station dropdowns from the database
        loadClasses(); // Load data into class dropdown from the database

        // updateBookingBadge(); // Removed from initialize to prevent NullPointerException
        // Badge logic should be handled separately, possibly in MyBookingsController
        // or triggered after the dashboard is fully loaded.

        // You might want to load the default dashboard home view here initially
        // loadDashboardView(); // Uncomment this if you want the home view to load on startup
    }

    public static BorderPane getMainPane() {
        return staticMainPane;
    }

    public static void loadBookingIntoDashboard(Parent bookingView) {
        if (instance != null && instance.mainPane != null) {
            System.out.println("✅ mainPane STATIC used and not null");
            instance.mainPane.setCenter(bookingView);
        } else {
            System.out.println("❌ mainPane is still null from static");
        }
    }

    // Public method to set the logged-in user's name
    public void setLoggedInUser(String username) {
        if (welcomeLabel != null) {
            welcomeLabel.setText("👋 Welcome, " + username + "!");
        }
    }

    @FXML
    private void onSearchClicked() {
        // Update the summary box with the user's selections
        updateSummary();

        String from = fromCombo.getValue();
        String to = toCombo.getValue();
        LocalDate date = datePicker.getValue();
        String travelClass = classCombo.getValue();

        if (from != null && !from.isEmpty() && to != null && !to.isEmpty() && date != null && travelClass != null && !travelClass.isEmpty()) {
            // Extract station codes from the dropdown values (e.g., "MAS, Chennai" -> "MAS")
            String fromCode = from.split(",")[0].trim();
            String toCode = to.split(",")[0].trim();

            // Simulate fetching train data (replace with your actual database logic)
            List<Train> searchResults = getSearchResults(fromCode, toCode, date, travelClass);
            displaySearchResults(searchResults);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Search");
            alert.setHeaderText(null);
            alert.setContentText("Please select From, To, Travel Date, and Class.");
            alert.showAndWait();
        }
    }

    private void displaySearchResults(List<Train> trains) {
        // Ensure resultsContainer and resultsScrollPane are correctly linked via fx:id if used here
        if (resultsContainer != null) { // Check if resultsContainer is injected
            resultsContainer.getChildren().clear(); // Clear previous search results
            if (trains.isEmpty()) {
                Label noResultsLabel = new Label("No trains found for your search criteria.");
                resultsContainer.getChildren().add(noResultsLabel);
            } else {
                for (Train train : trains) {
                    VBox trainCard = createTrainCard(train);
                    resultsContainer.getChildren().add(trainCard);
                }
            }
        }
        // Ensure resultsScrollPane is correctly linked via fx:id if used here
        if (resultsScrollPane != null) { // Check if resultsScrollPane is injected
            resultsScrollPane.setVisible(!trains.isEmpty()); // Make the scroll pane visible only if there are results
        }
    }

    private VBox createTrainCard(Train train) {
        // This method creates the UI for a single train search result
        VBox card = new VBox(10);
        card.getStyleClass().add("train-card");
        card.setPrefWidth(600); // Adjust width as needed
        card.setPadding(new Insets(15));

        Label trainNameLabel = new Label(train.getName());
        trainNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #007bff;"); // Prominent train name

        // Using train.getTravelClass() for displaying class
        Label trainDetailsLabel = new Label(train.getFromLocation() + " to " + train.getToLocation() + " | Class: " + train.getTravelClass());
        trainDetailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");


        HBox departureArrivalBox = new HBox(20);

        VBox departureBox = new VBox(5);
        Label departureTimeLabel = new Label(train.getDepartureTime() + " (Dep)"); // Example format
        departureTimeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        // Label departureStationLabel = new Label(train.getFromLocation().split(",")[1].trim()); // Use full location
        // departureStationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        departureBox.getChildren().addAll(departureTimeLabel); // Only show time and location in detailsLabel

        Label durationLabel = new Label("Duration: ?"); // You might need to calculate or fetch duration
        durationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");

        VBox arrivalBox = new VBox(5);
        Label arrivalTimeLabel = new Label(train.getArrivalTime() + " (Arr)"); // Example format
        arrivalTimeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        // Label arrivalStationLabel = new Label(train.getToLocation().split(",")[1].trim()); // Use full location
        // arrivalStationLabel.setStyle("-fx-size: 14px; -fx-text-fill: #333;");
        arrivalBox.getChildren().addAll(arrivalTimeLabel); // Only show time and location in detailsLabel

        departureArrivalBox.getChildren().addAll(departureBox, durationLabel, arrivalBox); // Simplified HBox content

        HBox classFareBox = new HBox(15);
        // Example of displaying class and fare
        VBox classFareOption = new VBox(5);
        Label classLabel = new Label(train.getTravelClass()); // Display class from Train object
        classLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #f0f0f0; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        Label fareLabel = new Label("₹" + String.format("%.2f", train.getFare()));
        fareLabel.setStyle("-fx-font-size: 14px;");
        classFareOption.getChildren().addAll(classLabel, fareLabel);
        classFareBox.getChildren().add(classFareOption);

        // Label additionalInfoLabel = new Label("GNWL 53, Free Cancellation, Updated 30 mins ago"); // Example info
        // additionalInfoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

        HBox buttonRow = new HBox();
        HBox.setHgrow(buttonRow, Priority.ALWAYS);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        Button bookButton = new Button("Book Ticket");
        bookButton.getStyleClass().add("search-button");
        // Call the method to load the booking details view and pass the train data
        bookButton.setOnAction(e -> loadBookingDetailsView(train, datePicker.getValue()));
        buttonRow.getChildren().add(bookButton);

        card.getChildren().addAll(trainNameLabel, trainDetailsLabel, departureArrivalBox, classFareBox, buttonRow); // Adjusted card content
        return card;
    }

    // Method to load the booking details view
    private void loadBookingDetailsView(Train train, LocalDate travelDate) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/railway/views/booking_details.fxml"));
            Parent bookingDetailsView = loader.load();

            BookingDetailsController controller = loader.getController();
            controller.setBookingData(train, travelDate);

            // ✅ Use static method to access the correct dashboardController
            dashboardController.loadBookingIntoDashboard(bookingDetailsView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void showBookingConfirmationPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Your Ticket was booked successfully!");
        DialogPane dialogPane = alert.getDialogPane();
        // Ensure CSS file path is correct
        java.net.URL cssUrl = getClass().getResource("/com/example/railway/css/dashboard.css");
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }
        dialogPane.getStyleClass().add("confirmation-dialog"); // You might need to add styles for this in your CSS
        alert.showAndWait();
    }

    // Badge related methods (commented out/removed as they were based on the old in-memory list)
    /* private void updateMyBookingsNotification() { ... } */
    /* public void updateBookingBadge() { ... } */


    @FXML
    private void updateSummary() {
        if (summaryBox != null) { // Check if summaryBox is injected
            summaryBox.getChildren().clear();

            if (fromCombo.getValue() != null && !fromCombo.getValue().isEmpty()) {
                summaryBox.getChildren().add(createSummaryLabel("FROM", fromCombo.getValue().split(",")[0].trim()));
            }

            if (fromCombo.getValue() != null && !fromCombo.getValue().isEmpty() && toCombo.getValue() != null && !toCombo.getValue().isEmpty()) {
                // Ensure image path is correct
                ImageView arrow = new ImageView(new Image(getClass().getResource("/com/example/railway/images/arrow.png").toExternalForm()));
                arrow.setFitWidth(32);
                arrow.setFitHeight(20);
                summaryBox.getChildren().add(arrow);
            }

            if (toCombo.getValue() != null && !toCombo.getValue().isEmpty()) {
                summaryBox.getChildren().add(createSummaryLabel("TO", toCombo.getValue().split(",")[0].trim()));
            }

            if (datePicker.getValue() != null) {
                String formattedDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("E, dd MMM yy"));
                summaryBox.getChildren().add(createSummaryLabel("DATE", formattedDate));
            }

            if (classCombo.getValue() != null && !classCombo.getValue().isEmpty() && !classCombo.getValue().equals("All Class")) {
                summaryBox.getChildren().add(createSummaryLabel("CLASS", classCombo.getValue()));
            }
        }
    }

    private VBox createSummaryLabel(String title, String value) {
        // This method creates the UI for a summary label
        Label labelTitle = new Label(title);
        labelTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

        Label labelValue = new Label(value);
        labelValue.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox box = new VBox(labelTitle, labelValue);
        box.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 8 12; -fx-background-radius: 8; -fx-border-color: #ccc; -fx-border-radius: 8;");
        return box;
    }


    // This method seems redundant with loadMyBookings, removing it
     /* @FXML
    private void handleMyBookings() throws IOException {
        VBox bookingsView = FXMLLoader.load(getClass().getResource("/com/example/railway/views/bookings.fxml"));
        mainPane.setCenter(bookingsView);
    } */


    @FXML
    private void loadMyBookings() throws IOException {
        // This method loads mybookings.fxml into the center
        // It needs to be linked to the My Bookings menu button onAction
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/railway/views/mybookings.fxml"));
        Parent bookingsView = loader.load();

        // Get controller and pass user email
        MyBookingsController controller = loader.getController();
        controller.loadBookingsForUser(LoggedInUser.getEmail()); // Assuming MyBookingsController has this method

        mainPane.setCenter(bookingsView);
    }


    @FXML
    private void loadDashboardView() {
        // Load the dedicated FXML for the default dashboard home view
        try {
            // Ensure dashboard_home.fxml exists and contains the default center content
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/railway/views/dashboard_home.fxml"));
            Parent dashboardHomeView = loader.load();
            mainPane.setCenter(dashboardHomeView);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error loading dashboard home view
            showAlert(Alert.AlertType.ERROR, "Could not load dashboard view.");
        }
    }

    private String getClassCodeByName(String className) {
        switch (className) {
            case "Sleeper": return "SL";
            case "AC 2 Tier": return "2A";
            case "AC 3 Tier": return "3A";
            default: return className;
        }

    }


    // Fetches train data from the database based on search criteria
    private List<Train> getSearchResults(String fromCode, String toCode, LocalDate date, String travelClass) {
        List<Train> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT train_name, departure_time, arrival_time, fare, class_code, from_station, to_station " +
                    "FROM trains WHERE from_station = ? AND to_station = ?";

            boolean filterByClass = travelClass != null && !travelClass.equals("All");
            if (filterByClass) {
                sql += " AND class_code = ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fromCode);
            stmt.setString(2, toCode);
            if (filterByClass) {
                stmt.setString(3, travelClass);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("train_name");
                String departureTime = rs.getString("departure_time");
                String arrivalTime = rs.getString("arrival_time");
                double fare = rs.getDouble("fare");
                String classCode = rs.getString("class_code");
                String fromStationCode = rs.getString("from_station");
                String toStationCode = rs.getString("to_station");

                String fromFull = findStationDisplayName(fromStationCode);
                String toFull = findStationDisplayName(toStationCode);

                results.add(new Train(name, departureTime, arrivalTime, fare, classCode, fromFull, toFull));
            }

            System.out.println("Searching for: " + fromCode + " to " + toCode + " on " + date + " class: " + travelClass);
            System.out.println("Results found: " + results.size());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error fetching train data from database.");
        }

        return results;
    }


    private String getClassFriendlyName(String code) {
        switch (code) {
            case "SL": return "Sleeper";
            case "2A": return "AC 2 Tier";
            case "3A": return "AC 3 Tier";
            case "CC": return "Chair Car";
            default: return code;
        }
    }





    // Helper method to find the full station name from dropdown items based on code
    private String findStationDisplayName(String code) {
        if (fromCombo == null || code == null || code.isEmpty()) return code; // Check if fromCombo is injected

        for (String item : fromCombo.getItems()) {
            if (item.startsWith(code + ",")) { // Match the start followed by a comma
                return item;
            }
        }
        return code; // Return code if full name not found
    }


    // Loads station data into From and To dropdowns from the database
    private void loadStations() {
        if (fromCombo == null || toCombo == null) return; // Ensure combos are injected

        fromCombo.getItems().clear();
        toCombo.getItems().clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Assuming your stations table has columns like station_code, station_name
            String sql = "SELECT station_code, station_name FROM stations ORDER BY station_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String code = rs.getString("station_code");
                String name = rs.getString("station_name");
                String display = code + ", " + name; // Format as "CODE, Name"
                fromCombo.getItems().add(display);
                toCombo.getItems().add(display);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error loading stations
            showAlert(Alert.AlertType.ERROR, "Database error loading stations.");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other errors
            showAlert(Alert.AlertType.ERROR, "Error loading stations.");
        }
    }

    // Loads class data into Class dropdown from the database
    private void loadClasses() {
        if (classCombo == null) return;

        classCombo.getItems().clear();
        classCombo.getItems().add("All"); // Optional: allow all class search

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT class_code FROM classes ORDER BY class_code";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classCombo.getItems().add(rs.getString("class_code")); // SL, 2A, etc.
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading travel classes.");
        }
    }


    // Helper method to show alerts
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}