package org.nitramproductions.com.wordclassifier.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.database.CSVManager;
import org.nitramproductions.com.wordclassifier.database.helper.Columns;

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
    private Button groupSelectSaveDestinationButton;
    @FXML
    private TextField groupSaveDestinationTextField;
    @FXML
    private Button groupExportButton;

    private final CSVManager csvManager = new CSVManager();

    public ExportToCSVController() {

    }

    @FXML
    private void initialize() {

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
    private void onCancelButtonClick() {

    }
}
