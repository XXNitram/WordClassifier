package org.nitramproductions.com.wordclassifier.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.database.CSVManager;
import org.nitramproductions.com.wordclassifier.database.helper.Columns;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

public class ExportToCSVController {

    @FXML
    private CheckBox groupNameCheckBox;
    @FXML
    private CheckBox groupDateModifiedCheckBox;
    @FXML
    private CheckBox groupCreationDateCheckBox;
    @FXML
    private Label groupSaveLocationLabel;
    @FXML
    private CheckBox expressionContentCheckBox;
    @FXML
    private CheckBox expressionDateModifiedCheckBox;
    @FXML
    private CheckBox expressionCreationDateCheckBox;
    @FXML
    private Label expressionSaveLocationLabel;
    @FXML
    private CheckBox belongToNameCheckBox;
    @FXML
    private CheckBox belongToContentCheckBox;
    @FXML
    private Label belongToSaveLocationLabel;

    private String groupSaveLocation;
    private String expressionSaveLocation;
    private String belongToSaveLocation;
    private final CSVManager csvManager = new CSVManager();
    private boolean darkMode;

    public ExportToCSVController() {

    }

    @FXML
    private void initialize() {
        Preferences preferences = Preferences.userRoot().node("/wordclassifier");
        darkMode = preferences.getBoolean("DARK_MODE", false);
    }

    @FXML
    private void onGroupSelectSaveLocationButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("gruppen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            groupSaveLocation = selectedFile.getAbsolutePath();
            groupSaveLocationLabel.setText(groupSaveLocation);
        }
    }

    @FXML
    private void onExpressionSelectSaveLocationButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("wörter");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            expressionSaveLocation = selectedFile.getAbsolutePath();
            expressionSaveLocationLabel.setText(expressionSaveLocation);
        }
    }

    @FXML
    private void onBelongsToSelectSaveLocationButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("zugehörigkeiten");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            belongToSaveLocation = selectedFile.getAbsolutePath();
            belongToSaveLocationLabel.setText(belongToSaveLocation);
        }
    }

    @FXML
    private void onGroupExportButtonClick(ActionEvent event) throws SQLException {
        if (!groupNameCheckBox.isSelected() && !groupDateModifiedCheckBox.isSelected() && !groupCreationDateCheckBox.isSelected()) {
            alertIfNoColumnIsSpecified();
            return;
        }
        if (groupSaveLocation == null || groupSaveLocation.isEmpty()) {
            alertIfNoSaveLocationIsSpecified();
            return;
        }
        List<Columns> groupColumns = new ArrayList<>();
        if (groupNameCheckBox.isSelected()) {
            groupColumns.add(Columns.NAME);
        }
        if (groupDateModifiedCheckBox.isSelected()) {
            groupColumns.add(Columns.DATE_MODIFIED);
        }
        if (groupCreationDateCheckBox.isSelected()) {
            groupColumns.add(Columns.CREATION_DATE);
        }
        csvManager.writeSpecificGroupColumnsToCSV(groupColumns, groupSaveLocation);
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onExpressionExportButtonClick(ActionEvent event) throws SQLException {
        if (!expressionContentCheckBox.isSelected() && !expressionDateModifiedCheckBox.isSelected() && !expressionCreationDateCheckBox.isSelected()) {
            alertIfNoColumnIsSpecified();
            return;
        }
        if (expressionSaveLocation == null || expressionSaveLocation.isEmpty()) {
            alertIfNoSaveLocationIsSpecified();
            return;
        }
        List<Columns> expressionColumns = new ArrayList<>();
        if (expressionContentCheckBox.isSelected()) {
            expressionColumns.add(Columns.CONTENT);
        }
        if (expressionDateModifiedCheckBox.isSelected()) {
            expressionColumns.add(Columns.DATE_MODIFIED);
        }
        if (expressionCreationDateCheckBox.isSelected()) {
            expressionColumns.add(Columns.CREATION_DATE);
        }
        csvManager.writeSpecificExpressionColumnsToCSV(expressionColumns, expressionSaveLocation);
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onBelongsToExportButtonClick(ActionEvent event) throws SQLException {
        if (!belongToNameCheckBox.isSelected() && !belongToContentCheckBox.isSelected()) {
            alertIfNoColumnIsSpecified();
            return;
        }
        if (belongToSaveLocation == null || belongToSaveLocation.isEmpty()) {
            alertIfNoSaveLocationIsSpecified();
            return;
        }
        List<Columns> belongToColumns = new ArrayList<>();
        if (belongToNameCheckBox.isSelected()) {
            belongToColumns.add(Columns.NAME);
        }
        if (belongToContentCheckBox.isSelected()) {
            belongToColumns.add(Columns.CONTENT);
        }
        csvManager.writeSpecificBelongToColumnsToCSV(belongToColumns, belongToSaveLocation);
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

    private void alertIfNoColumnIsSpecified() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte mindestens eine Spalte auswählen!");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("book-icon.png"))));
        if (darkMode) {
            stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
        alert.showAndWait();
    }

    private void alertIfNoSaveLocationIsSpecified() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte einen Speicherort wählen!");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("book-icon.png"))));
        if (darkMode) {
           stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
        alert.showAndWait();
    }
}
