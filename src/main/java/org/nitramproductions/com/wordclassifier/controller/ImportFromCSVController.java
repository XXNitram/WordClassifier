package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.controller.helper.AlertHelper;
import org.nitramproductions.com.wordclassifier.database.CSVManager;

import java.io.File;
import java.sql.SQLException;

public class ImportFromCSVController {

    @FXML
    private RadioButton groupRadioButton;
    @FXML
    private RadioButton expressionRadioButton;
    @FXML
    private RadioButton belongToRadioButton;
    @FXML
    private Label fileLocationLabel;

    private String fileLocation;
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final CSVManager csvManager = new CSVManager();
    private final AlertHelper alertHelper = new AlertHelper();
    private final MainController mainController;

    public ImportFromCSVController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        groupRadioButton.setToggleGroup(toggleGroup);
        expressionRadioButton.setToggleGroup(toggleGroup);
        belongToRadioButton.setToggleGroup(toggleGroup);
        Platform.runLater(() -> fileLocationLabel.getParent().requestFocus());
    }

    @FXML
    private void onSelectFileLocationButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            fileLocation = selectedFile.getAbsolutePath();
            fileLocationLabel.setText(fileLocation);
        }
    }

    @FXML
    private void onFormatRequirementsHyperlinkClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setPrefSize(450,660);
        alert.setTitle("Formatierungsvorgabe");
        alert.setHeaderText("Information zur Formatierung");
        alert.setContentText("""
                Allgemein sind keine Sonderzeichen au??er
                !"#$%&'()*+,-./:;=<>?@[\\]^_`{|}~ erlaubt!
                \s
                Notwendige Spalten bei Gruppen: NAME, DATE_MODIFIED, CREATION_DATE
                NAME muss zwingend angegeben werden!
                Beispiel:
                NAME,DATE_MODIFIED,CREATION_DATE
                Emotionen,2022-01-01 00:00:00,2022-01-01
                \s
                ??nderungsdatum und/oder Erstelldatum kann optional ausgelassen werden
                Beispiel:
                NAME,DATE_MODIFIED,CREATION_DATE
                B??cher
                \s
                Notwendige Spalten bei W??rtern: CONTENT, DATE_MODIFIED, CREATION_DATE
                CONTENT muss zwingend angegeben werden!
                Beispiel:
                CONTENT,DATE_MODIFIED,CREATION_DATE
                Wut,2022-01-01 00:00:00,2022-01-01 00:00:00
                \s
                ??nderungsdatum und/oder Erstelldatum kann optional ausgelassen werden
                Beispiel:
                CONTENT,DATE_MODIFIED,CREATION_DATE
                The Shining
                \s
                Notwendige Spalten bei Zugeh??rigkeiten: NAME, CONTENT
                NAME und CONTENT muss zwingend angegeben werden!
                Beispiel:
                NAME,CONTENT
                Emotionen,Wut
                B??cher,The Shining""");
        alert.initOwner(stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setResizable(false);
        alertHelper.setPositionToCenterOfParentStage(alert, stage);
        alertHelper.setIcon(alert);
        alertHelper.setDarkMode(alert);
        alert.showAndWait();
    }

    @FXML
    private void onImportButtonClick(ActionEvent event) throws SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        if (toggleGroup.getSelectedToggle() == null) {
            alertIfNoTableIsSpecified(stage);
            return;
        }
        if (fileLocation == null || fileLocation.isEmpty()) {
            alertIfNoFileLocationIsSpecified(stage);
            return;
        }
        if (toggleGroup.getSelectedToggle() == groupRadioButton) {
            csvManager.readAndInsertGroupsFromCSV(fileLocation);
        } else if (toggleGroup.getSelectedToggle() == expressionRadioButton) {
            csvManager.readAndInsertExpressionFromCSV(fileLocation);
        } else if (toggleGroup.getSelectedToggle() == belongToRadioButton) {
            csvManager.readAndInsertBelongsToFromCSV(fileLocation);
        }
        mainController.reloadData();
        stage.close();
    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    private void alertIfNoTableIsSpecified(Stage stage) {
        Alert alert = alertHelper.createNewErrorAlert("Bitte eine Tabelle ausw??hlen!", stage);
        alert.showAndWait();
    }

    private void alertIfNoFileLocationIsSpecified(Stage stage) {
        Alert alert = alertHelper.createNewErrorAlert("Bitte einen Speicherort w??hlen!", stage);
        alert.showAndWait();
    }
}
