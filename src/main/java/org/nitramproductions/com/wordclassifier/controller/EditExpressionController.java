package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.synedra.validatorfx.TooltipWrapper;
import org.nitramproductions.com.wordclassifier.controller.helper.*;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditExpressionController {

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
    private TableView<Group> leftTableView;
    @FXML
    private TableColumn<Group, String> leftTableViewNameColumn;
    @FXML
    private TableView<Group> rightTableView;
    @FXML
    private TableColumn<Group, String> rightTableViewNameColumn;

    private ObservableList<Group> leftList;
    private FilteredList<Group> filteredLeftList;
    private ObservableList<Group> rightList;
    private FilteredList<Group> filteredRightList;

    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SelectionHelper selectionHelper = new SelectionHelper();
    private final ValidationHelper validationHelper = new ValidationHelper();
    private final SearchHelper searchHelper = new SearchHelper();
    private final TooltipForTextFieldHelper tooltipForTextFieldHelper = new TooltipForTextFieldHelper();

    private  final MainController mainController;
    private final Expression expressionToEdit;
    private List<Group> groupsBelongingToExpressionList;

    public EditExpressionController(MainController mainController, Expression expressionToEdit) {
        this.mainController = mainController;
        this.expressionToEdit = expressionToEdit;
    }

    @FXML
    private void initialize() throws SQLException {
        groupsBelongingToExpressionList = connectionManager.getGroupsBelongingToExpression(expressionToEdit);
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
        leftList = FXCollections.observableArrayList(connectionManager.getAllGroups());
        leftList.removeAll(groupsBelongingToExpressionList);
        filteredLeftList = searchHelper.transformListsAndSetTableView(leftList, leftTableView);
        rightList = FXCollections.observableArrayList(groupsBelongingToExpressionList);
        filteredRightList = searchHelper.transformListsAndSetTableView(rightList, rightTableView);
    }

    private void initializeTableViews() {
        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initializeTableViewColumns() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        leftTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
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
        createNewWrapper = validationHelper.getTooltipWrapper(saveButton, "Wort kann nicht gespeichert werden:");
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);
    }

    private void initializeNameField() {
        nameTextField.setText(expressionToEdit.getContent());
        Platform.runLater(() -> {
            nameTextField.getParent().requestFocus();
            tooltipForTextFieldHelper.setTooltipForTextFieldIfContentExceedsWidth(nameTextField);
        });
        tooltipForTextFieldHelper.setTooltipForTextFieldIfContentExceedsWidthAndListenToChanges(nameTextField);
    }

    private void initializeDateLabels() {
        modificationDateLabel.setText(expressionToEdit.getFormattedDateModified());
        creationDateLabel.setText(expressionToEdit.getFormattedCreationDate());
    }

    private void searchTableViews() {
        searchHelper.searchFilteredGroupListForNameOnly(leftTableViewSearchTextField, filteredLeftList);
        searchHelper.searchFilteredGroupListForNameOnly(rightTableViewSearchTextField, filteredRightList);
    }

    private void validateNewNameTextField() throws SQLException {
        validationHelper.checkIfEmpty(nameTextField);
        validationHelper.checkIfIncludesSpecialCharacter(nameTextField);
        validationHelper.checkIfTooLong(nameTextField);
        List<Expression> expressions = connectionManager.getAllExpressions();
        expressions.remove(expressionToEdit);
        validationHelper.checkIfExpressionAlreadyExists(nameTextField, expressions);
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
        String newExpressionName = nameTextField.getText().trim();
        List<Group> belongingGroupsToDelete = new ArrayList<>(groupsBelongingToExpressionList);
        List<Group> belongingGroupsToAdd = new ArrayList<>(rightList);
        belongingGroupsToDelete.removeAll(rightList);
        belongingGroupsToAdd.removeAll(groupsBelongingToExpressionList);

        if (newExpressionName.equals(expressionToEdit.getContent()) && belongingGroupsToAdd.isEmpty() && belongingGroupsToDelete.isEmpty()) {
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
            return;
        }
        if (!newExpressionName.equals(expressionToEdit.getContent())) {
            connectionManager.updateExpressionName(expressionToEdit, newExpressionName);
            expressionToEdit.setContent(newExpressionName);
        }
        if (!belongingGroupsToDelete.isEmpty()) {
            for (Group group : belongingGroupsToDelete) {
                connectionManager.deleteBelongToRelation(group, expressionToEdit);
                connectionManager.updateGroupModificationDate(group);
            }
        }
        if (!belongingGroupsToAdd.isEmpty()) {
            for (Group group : belongingGroupsToAdd) {
                connectionManager.addNewBelongToRelation(group, expressionToEdit);
                connectionManager.updateGroupModificationDate(group);
            }
        }
        connectionManager.updateExpressionModificationDate(expressionToEdit);
        mainController.reloadData();
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
