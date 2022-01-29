package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private final BooleanProperty needToReloadData;

    public ImportFromCSVController(BooleanProperty needToReloadData) {
        this.needToReloadData = needToReloadData;
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
    private void onFormatRequirementsHyperlinkClick() {

    }

    @FXML
    private void onImportButtonClick(ActionEvent event) throws SQLException {
        if (toggleGroup.getSelectedToggle() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte eine Tabelle auswählen!");
            alert.showAndWait();
            return;
        }
        if (fileLocation == null || fileLocation.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte einen Speicherort wählen!");
            alert.showAndWait();
            return;
        }
        if (toggleGroup.getSelectedToggle() == groupRadioButton) {
            csvManager.readAndInsertGroupsFromCSV(fileLocation);
        } else if (toggleGroup.getSelectedToggle() == expressionRadioButton) {
            csvManager.readAndInsertExpressionFromCSV(fileLocation);
        } else if (toggleGroup.getSelectedToggle() == belongToRadioButton) {
            csvManager.readAndInsertBelongsToFromCSV(fileLocation);
        }
        needToReloadData.set(true);
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
