package org.nitramproductions.com.wordclassifier.controller;

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
import java.util.List;

public class CreateExpressionController {

    @FXML
    private ButtonBar buttonBar;
    @FXML
    private TextField newNameTextField;
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

    private final Button createNewButton = new Button("Erstellen");
    private final Button cancelButton = new Button("Abbrechen");

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SelectionHelper selectionHelper = new SelectionHelper();
    private final ValidationHelper validationHelper = new ValidationHelper();
    private final SearchHelper searchHelper = new SearchHelper();
    private final TooltipForTextFieldHelper tooltipForTextFieldHelper = new TooltipForTextFieldHelper();
    private final MainController mainController;

    public CreateExpressionController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() throws SQLException {
        initializeLists();
        initializeTableViews();
        initializeTableViewColumns();
        initializeTextField();
        initializeButtons();
        searchTableViews();
        validateNewNameTextField();
        deselectListIfAnotherIsSelected();
    }

    private void initializeLists() throws SQLException {
        leftList = FXCollections.observableArrayList(connectionManager.getAllGroups());
        filteredLeftList = searchHelper.transformListsAndSetTableView(leftList, leftTableView);
        rightList = FXCollections.observableArrayList();
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

    private void initializeTextField() {
        tooltipForTextFieldHelper.setTooltipForTextFieldIfContentExceedsWidthAndListenToChanges(newNameTextField);
    }

    private void initializeButtons() {
        cancelButton.setCancelButton(true);
        createNewButton.setDefaultButton(true);
        cancelButton.translateXProperty().set(-20);
        createNewButton.translateXProperty().set(-25);
        cancelButton.setOnAction(e -> onCancelButtonClick());
        createNewButton.setOnAction(e -> onCreateNewButtonClick());

        TooltipWrapper<Button> createNewWrapper;
        createNewWrapper = validationHelper.getTooltipWrapper(createNewButton, "Wort kann nicht erstellt werden:");
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);
    }

    private void searchTableViews() {
        searchHelper.searchFilteredGroupListForNameOnly(leftTableViewSearchTextField, filteredLeftList);
        searchHelper.searchFilteredGroupListForNameOnly(rightTableViewSearchTextField, filteredRightList);
    }

    private void validateNewNameTextField() throws SQLException {
        validationHelper.checkIfEmpty(newNameTextField);
        validationHelper.checkIfIncludesSpecialCharacter(newNameTextField);
        validationHelper.checkIfTooLong(newNameTextField);
        List<Expression> expressions = connectionManager.getAllExpressions();
        validationHelper.checkIfExpressionAlreadyExists(newNameTextField, expressions);
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

    private void onCreateNewButtonClick() {
        String newExpressionName = newNameTextField.getText().trim();
        Expression newExpression = new Expression(newExpressionName);
        try {
            connectionManager.addNewExpression(newExpression);
            if (!rightList.isEmpty()) {
                for (Group group : rightList) {
                    connectionManager.addNewBelongToRelation(group, newExpression);
                    connectionManager.updateGroupModificationDate(group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mainController.reloadData();
        Stage stage = (Stage) createNewButton.getScene().getWindow();
        stage.close();
    }

    private void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
