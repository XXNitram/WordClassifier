package org.nitramproductions.com.wordclassifier.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

    }

    @FXML
    private void onGroupExportButtonClick(ActionEvent event) throws SQLException {
        if (!groupNameCheckBox.isSelected() && !groupDateModifiedCheckBox.isSelected() && !groupCreationDateCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte mindestens eine Spalte auswählen!");
            alert.showAndWait();
            return;
        }
        if (alertIfNoFileLocationIsSpecified(groupSaveLocation)) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte mindestens eine Spalte auswählen!");
            alert.showAndWait();
            return;
        }
        if (alertIfNoFileLocationIsSpecified(expressionSaveLocation)) {
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
    private void onBelongsToExportButtonClick(ActionEvent event) {

    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    private boolean alertIfNoFileLocationIsSpecified(String saveLocation) {
        if (saveLocation == null || saveLocation.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bitte einen Speicherort wählen!");
            alert.showAndWait();
            return true;
        }
        return false;
    }
}
