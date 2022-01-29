package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.database.CSVManager;

import java.io.File;

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

    public ImportFromCSVController() {

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
    private void onImportButtonClick() {

    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
