package org.nitramproductions.com.wordclassifier.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
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
    private Button groupExportButton;

    private String groupSaveLocation;
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
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            groupSaveLocation = selectedDirectory.getAbsolutePath();
            groupSaveLocationLabel.setText(groupSaveLocation);
        }
    }

    @FXML
    private void onGroupExportButtonClick() throws SQLException {
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
        csvManager.writeSpecificGroupColumnsToCSV(groupColumns);
        Stage stage = (Stage) groupExportButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
