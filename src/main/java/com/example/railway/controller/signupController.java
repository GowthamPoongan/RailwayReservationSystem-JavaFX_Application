package com.example.railway.controller;

import com.example.railway.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class signupController {

    @FXML private TextField firstNameField, lastNameField, emailField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleSignup() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("❌ Passwords do not match!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                statusLabel.setText("⚠️ Email is already registered.");
                return;
            }

            String insertSql = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAutoRedirectPopup();
            } else {
                statusLabel.setText("❌ Signup failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Database error: " + e.getMessage());
        }
    }

    private void showAutoRedirectPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("✅ Account Created Successfully!");
        alert.setContentText("Redirecting to login in 5 seconds...");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-radius: 12; -fx-border-radius: 12;");
        dialogPane.getStylesheets().add(getClass().getResource("/com/example/railway/css/dashboard.css").toExternalForm());

        alert.show();

        // Countdown Timer (5 seconds)
        new Thread(() -> {
            try {
                for (int i = 5; i >= 1; i--) {
                    final int secondsLeft = i;
                    javafx.application.Platform.runLater(() -> {
                        alert.setContentText("Redirecting to login in " + secondsLeft + " seconds...");
                    });
                    Thread.sleep(1000);
                }
                javafx.application.Platform.runLater(() -> {
                    alert.close();
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/com/example/railway/views/login.fxml"));
                        Stage stage = (Stage) firstNameField.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Login");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }



    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/railway/views/login.fxml"));
            Parent root = loader.load();

            Scene loginScene = new Scene(root);
            loginScene.getStylesheets().add(getClass().getResource("/com/example/railway/css/login.css").toExternalForm());

            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
