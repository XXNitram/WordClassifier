package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.prefs.Preferences;

public class AlertHelper {

    public AlertHelper() {

    }

    public Alert createNewErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
        setIcon(alert);
        setDarkMode(alert);
        return alert;
    }

    public void setIcon(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("book-icon.png"))));
    }

    public void setDarkMode(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        if (darkMode()) {
            stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
    }

    private boolean darkMode() {
        Preferences preferences = Preferences.userRoot().node("/wordclassifier");
        return preferences.getBoolean("DARK_MODE", false);
    }
}
