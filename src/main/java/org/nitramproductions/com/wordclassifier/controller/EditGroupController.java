package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.synedra.validatorfx.TooltipWrapper;
import org.nitramproductions.com.wordclassifier.controller.helper.SearchHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.SelectionHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.TooltipForEllipsizedCells;
import org.nitramproductions.com.wordclassifier.controller.helper.ValidationHelper;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditGroupController {

    @FXML
    private ButtonBar buttonBar;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label creationDateLabel;
    @FXML
    private Label modificationDateLabel;
    @FXML
    private TextField leftTableViewSearchTextField;
    @FXML
    private TextField rightTableViewSearchTextField;
    @FXML
    private TableView<Expression> leftTableView;
    @FXML
    private TableColumn<Expression, String> leftTableViewNameColumn;
    @FXML
    private TableView<Expression> rightTableView;
    @FXML
    private TableColumn<Expression, String> rightTableViewNameColumn;

    private ObservableList<Expression> leftList;
    private FilteredList<Expression> filteredLeftList;
    private ObservableList<Expression> rightList;
    private FilteredList<Expression> filteredRightList;

    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SelectionHelper selectionHelper = new SelectionHelper();
    private final ValidationHelper validationHelper = new ValidationHelper();
    private final SearchHelper searchHelper = new SearchHelper();

    private final MainController mainController;
    private final Group groupToEdit;
    private List<Expression> expressionsBelongingToGroupList;

    public EditGroupController(MainController mainController, Group groupToEdit) {
        this.groupToEdit = groupToEdit;
        this.mainController = mainController;
    }

    @FXML
    private void initialize() throws SQLException {
        expressionsBelongingToGroupList = connectionManager.getExpressionsBelongingToGroup(groupToEdit);
        initializeLists();
        initializeTableViews();
        initializeTableViewColumns();
        initializeButtons();
        initializeNameField();
        initializeDateLabels();
        searchTableViews();
        validateNewNameTextField();
        deselectListIfAnotherIsSelected();
    }

    private void initializeLists() throws SQLException {
        leftList = FXCollections.observableArrayList(connectionManager.getAllExpressions());
        leftList.removeAll(expressionsBelongingToGroupList);
        filteredLeftList = searchHelper.transformListsAndSetTableView(leftList, leftTableView);
        rightList = FXCollections.observableArrayList(expressionsBelongingToGroupList);
        filteredRightList = searchHelper.transformListsAndSetTableView(rightList, rightTableView);
    }

    private void initializeTableViews() {
        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initializeTableViewColumns() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        leftTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
    }

    private void initializeButtons() {
        cancelButton.setCancelButton(true);
        saveButton.setDefaultButton(true);
        cancelButton.translateXProperty().set(-20);
        saveButton.translateXProperty().set(-25);
        cancelButton.setOnAction(e -> onCancelButtonClick());
        saveButton.setOnAction(e -> {
            try {
                onSaveButtonClick();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        TooltipWrapper<Button> createNewWrapper;
        createNewWrapper = validationHelper.getTooltipWrapper(saveButton, "Gruppe kann nicht gespeichert werden:");
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);
    }

    private void initializeNameField() {
        nameTextField.setText(groupToEdit.getName());
        Platform.runLater(() -> nameTextField.getParent().requestFocus());
    }

    private void initializeDateLabels() {
        modificationDateLabel.setText(groupToEdit.getFormattedDateModified());
        creationDateLabel.setText(groupToEdit.getFormattedCreationDate());
    }

    private void searchTableViews() {
        searchHelper.searchFilteredExpressionListForNameOnly(leftTableViewSearchTextField, filteredLeftList);
        searchHelper.searchFilteredExpressionListForNameOnly(rightTableViewSearchTextField, filteredRightList);
    }

    private void validateNewNameTextField() throws SQLException {
        validationHelper.checkIfEmpty(nameTextField);
        validationHelper.checkIfIncludesSpecialCharacter(nameTextField);
        validationHelper.checkIfTooLong(nameTextField);
        List<Group> groups = connectionManager.getAllGroups();
        groups.remove(groupToEdit);
        validationHelper.checkIfGroupAlreadyExists(nameTextField, groups);
    }

    private void deselectListIfAnotherIsSelected() {
        selectionHelper.deselectEitherTableViewIfOtherGetsSelected(leftTableView, rightTableView);
    }

    @FXML
    private void onRightArrowButtonClick() {
        selectionHelper.transferSelectedItemsToAnotherList(leftTableView, leftList, rightList);
    }

    @FXML
    private void onLeftArrowButtonClick() {
        selectionHelper.transferSelectedItemsToAnotherList(rightTableView, rightList, leftList);
    }

    private void onSaveButtonClick() throws SQLException {
        String newGroupName = nameTextField.getText().trim();
        List<Expression> belongingExpressionsToDelete = new ArrayList<>(expressionsBelongingToGroupList);
        List<Expression> belongingExpressionsToAdd = new ArrayList<>(rightList);
        belongingExpressionsToDelete.removeAll(rightList);
        belongingExpressionsToAdd.removeAll(expressionsBelongingToGroupList);

        if (newGroupName.equals(groupToEdit.getName()) && belongingExpressionsToAdd.isEmpty() && belongingExpressionsToDelete.isEmpty()) {
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
            return;
        }
        if (!newGroupName.equals(groupToEdit.getName())) {
            connectionManager.updateGroupName(groupToEdit, newGroupName);
            groupToEdit.setName(newGroupName);
        }
        if (!belongingExpressionsToDelete.isEmpty()) {
            for (Expression expression : belongingExpressionsToDelete) {
                connectionManager.deleteBelongToRelation(groupToEdit, expression);
                connectionManager.updateExpressionModificationDate(expression);
            }
        }
        if (!belongingExpressionsToAdd.isEmpty()) {
            for (Expression expression : belongingExpressionsToAdd) {
                connectionManager.addNewBelongToRelation(groupToEdit, expression);
                connectionManager.updateExpressionModificationDate(expression);
            }
        }
        connectionManager.updateGroupModificationDate(groupToEdit);
        mainController.reloadData();
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
