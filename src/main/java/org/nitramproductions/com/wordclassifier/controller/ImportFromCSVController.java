package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.database.CSVManager;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;

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
    private void onFormatRequirementsHyperlinkClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setPrefSize(450,660);
        alert.setTitle("Formatierungsvorgabe");
        alert.setHeaderText("Information zur Formatierung");
        alert.setContentText("""
                Allgemein sind keine Sonderzeichen außer
                !"#$%&'()*+,-./:;=<>?@[\\]^_`{|}~ erlaubt!
                \s
                Notwendige Spalten bei Gruppen: NAME, DATE_MODIFIED, CREATION_DATE
                NAME muss zwingend angegeben werden!
                Beispiel:
                NAME,DATE_MODIFIED,CREATION_DATE
                Emotionen,2022-01-01 00:00:00,2022-01-01
                \s
                Änderungsdatum und/oder Erstelldatum kann optional ausgelassen werden
                Beispiel:
                NAME,DATE_MODIFIED,CREATION_DATE
                Bücher
                \s
                Notwendige Spalten bei Wörtern: CONTENT, DATE_MODIFIED, CREATION_DATE
                CONTENT muss zwingend angegeben werden!
                Beispiel:
                CONTENT,DATE_MODIFIED,CREATION_DATE
                Wut,2022-01-01 00:00:00,2022-01-01 00:00:00
                \s
                Änderungsdatum und/oder Erstelldatum kann optional ausgelassen werden
                Beispiel:
                CONTENT,DATE_MODIFIED,CREATION_DATE
                The Shining
                \s
                Notwendige Spalten bei Zugehörigkeiten: NAME, CONTENT
                NAME und CONTENT muss zwingend angegeben werden!
                Beispiel:
                NAME,CONTENT
                Emotionen,Wut
                Bücher,The Shining""");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("book-icon.png"))));
        alert.showAndWait();
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
        mainController.reloadData();
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
