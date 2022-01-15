package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.nitramproductions.com.wordclassifier.controller.helper.SelectionHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.SearchHelper;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class MainController {

    @FXML
    private TableView<Group> leftTableView;
    @FXML
    private TableColumn<Group, String> leftTableViewNameColumn;
    @FXML
    private TableColumn<Group, LocalDateTime> leftTableViewDateModifiedColumn;
    @FXML
    private TableView<Expression> rightTableView;
    @FXML
    private TableColumn<Expression, String> rightTableViewNameColumn;
    @FXML
    private TableColumn<Expression, LocalDateTime> rightTableViewDateModifiedColumn;
    @FXML
    private ChoiceBox<String> leftTableViewChoiceBox;
    @FXML
    private ChoiceBox<String> rightTableViewChoiceBox;
    @FXML
    private TextField leftTableViewTextField;
    @FXML
    private TextField rightTableViewTextField;
    @FXML
    private MenuBar menuBar;
    @FXML
    private CheckMenuItem darkMode;
    @FXML
    private ToggleSwitch toggleSwitch;

    private ObservableList<Group> observableGroupList;
    private FilteredList<Group> filteredGroupList;
    private ObservableList<Expression> observableExpressionList;
    private FilteredList<Expression> filteredExpressionList;

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SearchHelper searchHelper = new SearchHelper();
    private final SelectionHelper selectionHelper = new SelectionHelper();
    private final BooleanProperty needToReloadData = new SimpleBooleanProperty(false);

    public MainController() throws SQLException, IOException {
        connectionManager.initialize();
    }

    @FXML
    private void initialize() throws SQLException {
        initializeChoiceBoxes();
        initializeTextFields();
        initializeGroupLists();
        initializeExpressionLists();
        initializeTableViewColumns();

        searchLeftTableView();
        searchRightTableView();

        deselectListIfAnotherIsSelected();
        updateLeftTableViewDependingOnSelectionInRightTableView();
        updateRightTableViewDependingOnSelectionInLeftTableView();
        listenToToggleSwitchAndUpdateTableView();
        reloadGroupData();
    }

    private void initializeGroupLists() throws SQLException {
        observableGroupList = FXCollections.observableArrayList(connectionManager.getAllGroups());
        filteredGroupList = searchHelper.transformListsAndSetTableView(observableGroupList, leftTableView);
    }

    private void initializeExpressionLists() throws SQLException {
        observableExpressionList = FXCollections.observableArrayList(connectionManager.getAllExpressions());
        filteredExpressionList = searchHelper.transformListsAndSetTableView(observableExpressionList, rightTableView);
    }

    private void initializeTableViewColumns() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> {
            String result = cellData.getValue().nameProperty().get().replaceAll("(.{35})", "$1\n");
            return new SimpleStringProperty(result);
        });
        leftTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().dateModifiedProperty());

        rightTableViewNameColumn.setCellValueFactory(cellData -> {
            String result = cellData.getValue().contentProperty().get().replaceAll("(.{35})", "$1\n");
            return new SimpleStringProperty(result);
        });
        rightTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().dateModifiedProperty());
    }

    private void initializeChoiceBoxes() {
        leftTableViewChoiceBox.getItems().addAll("Name");
        leftTableViewChoiceBox.getSelectionModel().select("Name");
        rightTableViewChoiceBox.getItems().addAll("Name");
        rightTableViewChoiceBox.getSelectionModel().select("Name");
    }

    private void initializeTextFields() {
        leftTableViewTextField.setPromptText("Gib hier ein Suchwort ein!");
        rightTableViewTextField.setPromptText("Gib hier ein Suchwort ein!");
    }

    private void searchLeftTableView() {
        searchHelper.searchFilteredGroupListDependingOnChoiceBox(leftTableViewTextField, leftTableViewChoiceBox, filteredGroupList);
        searchHelper.clearTextFieldIfChoiceBoxChanged(leftTableViewChoiceBox, leftTableViewTextField);
    }

    private void searchRightTableView() {
        searchHelper.searchFilteredExpressionListDependingOnChoiceBox(rightTableViewTextField, rightTableViewChoiceBox, filteredExpressionList);
        searchHelper.clearTextFieldIfChoiceBoxChanged(rightTableViewChoiceBox, rightTableViewTextField);
    }

    private void deselectListIfAnotherIsSelected() {
        selectionHelper.deselectEitherTableViewIfOtherGetsSelected(leftTableView, rightTableView);
    }

    private void updateRightTableViewDependingOnSelectionInLeftTableView() {
        leftTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (!toggleSwitch.isSelected()) {
                    try {
                        observableExpressionList.clear();
                        observableExpressionList.addAll(connectionManager.getExpressionsBelongingToGroup(newSelection));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateLeftTableViewDependingOnSelectionInRightTableView() {
        rightTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (toggleSwitch.isSelected()) {
                    try {
                        observableGroupList.clear();
                        observableGroupList.addAll(connectionManager.getGroupsBelongingToExpression(newSelection));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void listenToToggleSwitchAndUpdateTableView() {
        toggleSwitch.selectedProperty().addListener((observableValue, oldSelection, newSelection) ->
                updateTableViewDependingOnToggleSwitch(newSelection));
    }

    private void reloadGroupData() {
        needToReloadData.addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection) {
                updateTableViewDependingOnToggleSwitch(toggleSwitch.isSelected());
                needToReloadData.set(false);
            }
        });
    }

    private void updateTableViewDependingOnToggleSwitch(Boolean expressionIsSwitchedOn) {
        clearAll();
        if (!expressionIsSwitchedOn) {
            try {
                observableGroupList.addAll(connectionManager.getAllGroups());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                observableExpressionList.addAll(connectionManager.getAllExpressions());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearAll() {
        leftTableViewTextField.setText("");
        rightTableViewTextField.setText("");
        observableGroupList.clear();
        leftTableView.getSelectionModel().clearSelection();
        observableExpressionList.clear();
        rightTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void onDeleteButtonClick() throws SQLException {
        if (!leftTableView.getSelectionModel().isEmpty()) {
            deleteSelectedGroup();
        }
        if (!rightTableView.getSelectionModel().isEmpty()) {
            deleteSelectedExpression();
        }
    }

    private void deleteSelectedGroup() throws SQLException {
        Group groupToDelete = leftTableView.getSelectionModel().getSelectedItem();
        connectionManager.deleteGroup(groupToDelete);
        observableGroupList.remove(groupToDelete);
        if (!toggleSwitch.isSelected()) {
            observableExpressionList.clear();
        }
    }

    private void deleteSelectedExpression() throws SQLException {
        Expression expressionToDelete = rightTableView.getSelectionModel().getSelectedItem();
        connectionManager.deleteExpression(expressionToDelete);
        observableExpressionList.remove(expressionToDelete);
        if (toggleSwitch.isSelected()) {
            observableGroupList.clear();
        }
    }

    @FXML
    private void onDarkModeCheckMenuClick() {
        Scene scene = menuBar.getScene();
        if (darkMode.isSelected()) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        } else {
            scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
    }

    @FXML
    private void onCreateNewGroupMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createGroup.fxml"));
        Parent root = fxmlLoader.load();
        CreateGroupController createGroupController = fxmlLoader.getController();
        createGroupController.initializeNeedToReloadDataBooleanProperty(needToReloadData);
        createNewStage(root, "Neue Gruppe Erstellen", 783, 440);
    }

    @FXML
    private void onCreateNewExpressionMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createExpression.fxml"));
        Parent root = fxmlLoader.load();
        CreateExpressionController createExpressionController = fxmlLoader.getController();
        createExpressionController.initializeNeedToReloadDataBooleanProperty(needToReloadData);
        createNewStage(root, "Neues Wort Erstellen", 783, 440);
    }

    @FXML
    private void onAboutMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about.fxml"));
        Parent root = fxmlLoader.load();
        AboutController aboutController = fxmlLoader.getController();
        aboutController.setDarkMode(darkMode.isSelected());
        createNewStage(root, "About", 600, 400);
    }

    private void createNewStage(Parent root, String title, int width, int height) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(menuBar.getParent().getScene().getWindow());
        Scene scene = new Scene(root, width, height);
        if (darkMode.isSelected()) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
        stage.setTitle(title);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("book-icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onCloseMenuItemClick() {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }
}
