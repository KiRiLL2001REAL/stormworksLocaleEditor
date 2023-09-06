package ru.example.localetool.view;

import javafx.scene.control.Alert;

public class DialogFactory {
    public static Alert buildWarningDialog(String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("LocaleTool");
        alert.setHeaderText("");
        alert.setContentText(contentText);
        return alert;
    }

    public static Alert buildConfirmationDialog(String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LocaleTool");
        alert.setHeaderText("");
        alert.setContentText(contentText);
        return alert;
    }
}
