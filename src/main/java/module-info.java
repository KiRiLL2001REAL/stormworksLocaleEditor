module ru.example.localetool {
    requires javafx.controls;
    requires javafx.fxml;
    requires ini4j;


    opens ru.example.localetool to javafx.fxml;
    exports ru.example.localetool;
    exports ru.example.localetool.controllers;
    opens ru.example.localetool.controllers to javafx.fxml;
}