module com.example.railway {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    opens com.example.railway to javafx.fxml;
    opens com.example.railway.controller to javafx.fxml;

    exports com.example.railway;
    exports com.example.railway.controller;
}
