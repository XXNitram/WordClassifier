package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.prefs.Preferences;

public class AlertHelper {

    public AlertHelper() {

    }

    public Alert createNewErrorAlert(String errorMessage, Stage parentStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
        alert.initOwner(parentStage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setResizable(false);
        setPositionToCenterOfParentStage(alert, parentStage);
        setIcon(alert);
        setDarkMode(alert);
        return alert;
    }

    public void setPositionToCenterOfParentStage(Alert alert, Stage parentStage) {
        alert.setX((parentStage.getX() + (parentStage.getWidth() / 2)) - (alert.getWidth() / 2));
        alert.setY((parentStage.getY() + (parentStage.getHeight() / 2)) - (alert.getHeight() / 2));
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
