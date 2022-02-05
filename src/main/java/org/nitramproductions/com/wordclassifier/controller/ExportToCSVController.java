package org.nitramproductions.com.wordclassifier.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.controller.helper.AlertHelper;
import org.nitramproductions.com.wordclassifier.database.CSVManager;
import org.nitramproductions.com.wordclassifier.database.helper.Columns;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    private final AlertHelper alertHelper = new AlertHelper();

    public ExportToCSVController() {

    }

    @FXML
    private void initialize() {

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
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        if (!groupNameCheckBox.isSelected() && !groupDateModifiedCheckBox.isSelected() && !groupCreationDateCheckBox.isSelected()) {
            alertIfNoColumnIsSpecified(stage);
            return;
        }
        if (groupSaveLocation == null || groupSaveLocation.isEmpty()) {
            alertIfNoSaveLocationIsSpecified(stage);
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
        stage.close();
    }

    @FXML
    private void onExpressionExportButtonClick(ActionEvent event) throws SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        if (!expressionContentCheckBox.isSelected() && !expressionDateModifiedCheckBox.isSelected() && !expressionCreationDateCheckBox.isSelected()) {
            alertIfNoColumnIsSpecified(stage);
            return;
        }
        if (expressionSaveLocation == null || expressionSaveLocation.isEmpty()) {
            alertIfNoSaveLocationIsSpecified(stage);
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
        stage.close();
    }

    @FXML
    private void onBelongsToExportButtonClick(ActionEvent event) throws SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        if (!belongToNameCheckBox.isSelected() && !belongToContentCheckBox.isSelected()) {
            alertIfNoColumnIsSpecified(stage);
            return;
        }
        if (belongToSaveLocation == null || belongToSaveLocation.isEmpty()) {
            alertIfNoSaveLocationIsSpecified(stage);
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
        stage.close();
    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    private void alertIfNoColumnIsSpecified(Stage stage) {
        Alert alert = alertHelper.createNewErrorAlert("Bitte mindestens eine Spalte auswählen!", stage);
        alert.showAndWait();
    }

    private void alertIfNoSaveLocationIsSpecified(Stage stage) {
        Alert alert = alertHelper.createNewErrorAlert("Bitte einen Speicherort wählen!", stage);
        alert.showAndWait();
    }
}
