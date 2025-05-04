// FILE: Main.java
package com.example.railway;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML
        java.net.URL fxmlUrl = Main.class.getResource("/com/example/railway/views/login.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("Cannot find login.fxml");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

        // Load CSS
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        java.net.URL cssUrl = getClass().getResource("/com/example/railway/css/login.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Warning: login.css not found");
        }

        stage.setTitle("Railway Reservation System - Login");
        stage.setScene(scene);
        stage.setResizable(true);

        stage.setMinWidth(800);
        stage.setMinHeight(400);

        stage.show();
    }
}
