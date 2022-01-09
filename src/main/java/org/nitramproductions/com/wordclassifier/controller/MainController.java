package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.ToggleSwitch;
import org.nitramproductions.com.wordclassifier.MainApplication;
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
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Group> observableGroupList;
    private FilteredList<Group> filteredGroupList;
    private SortedList<Group> sortedGroupList;

    private ObservableList<Expression> observableExpressionList;
    private FilteredList<Expression> filteredExpressionList;
    private SortedList<Expression> sortedExpressionList;

    private BooleanProperty needToReloadData = new SimpleBooleanProperty(false);

    public MainController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        leftTableViewChoiceBox.getItems().clear();
        leftTableViewChoiceBox.getItems().addAll("Name");
        leftTableViewChoiceBox.getSelectionModel().select("Name");
        rightTableViewChoiceBox.getItems().clear();
        rightTableViewChoiceBox.getItems().addAll("Name");
        rightTableViewChoiceBox.getSelectionModel().select("Name");

        leftTableViewTextField.setPromptText("Gib hier ein Suchwort ein!");
        rightTableViewTextField.setPromptText("Gib hier ein Suchwort ein!");

        observableGroupList = FXCollections.observableArrayList(ConnectionManager.getAllGroups());
        updateGroupLists();
        updateGroupListsIfChange();

        observableExpressionList = FXCollections.observableArrayList(ConnectionManager.getAllExpressions());
        updateExpressionLists();
        updateExpressionListsIfChange();

        searchLeftTableView();
        searchRightTableView();
        updateLeftTableViewDependingOnSelectionInRightTableView();
        updateRightTableViewDependingOnSelectionInLeftTableView();
        listenToToggleSwitchAndUpdateTableView();

        reloadGroupData();

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

    public void updateGroupLists() {
        filteredGroupList = new FilteredList<>(observableGroupList, group -> true);
        sortedGroupList = new SortedList<>(filteredGroupList);
        sortedGroupList.comparatorProperty().bind(leftTableView.comparatorProperty());
        leftTableView.setItems(sortedGroupList);
    }

    public void updateExpressionLists() {
        filteredExpressionList = new FilteredList<>(observableExpressionList, expression -> true);
        sortedExpressionList = new SortedList<>(filteredExpressionList);
        sortedExpressionList.comparatorProperty().bind(rightTableView.comparatorProperty());
        rightTableView.setItems(sortedExpressionList);
    }

    public void updateGroupListsIfChange() {
        observableGroupList.addListener((ListChangeListener<Group>) change -> {
            if (change.next()) {
                updateGroupLists();
            }
        });
    }

    public void updateExpressionListsIfChange() {
        observableExpressionList.addListener((ListChangeListener<Expression>) change -> {
            if (change.next()) {
                updateExpressionLists();
            }
        });
    }

    public void reloadGroupData() {
        needToReloadData.addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection) {
                updateTableViewDependingOnToggleSwitch(toggleSwitch.isSelected());
                needToReloadData.set(false);
            }
        });
    }

    public void searchLeftTableView() {
        leftTableViewTextField.textProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if ("Name".equals(leftTableViewChoiceBox.getValue())) {
                filteredGroupList.setPredicate(group -> group.getName().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
        });

        leftTableViewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                leftTableViewTextField.setText("");
            }
        });
    }

    public void searchRightTableView() {
        rightTableViewTextField.textProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if ("Name".equals(rightTableViewChoiceBox.getValue())) {
                filteredExpressionList.setPredicate(expression -> expression.getContent().toLowerCase().contains(newSelection.toLowerCase().trim()));
            }
        });

        rightTableViewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                rightTableViewTextField.setText("");
            }
        });
    }

    public void updateRightTableViewDependingOnSelectionInLeftTableView() {
        leftTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                rightTableView.getSelectionModel().clearSelection();
                if (!toggleSwitch.isSelected()) {
                    try {
                        observableExpressionList.clear();
                        observableExpressionList.addAll(ConnectionManager.getExpressionsFromGroup(newSelection));
                        if ("Name".equals(leftTableViewChoiceBox.getValue())) {
                            filteredExpressionList.setPredicate(expression -> expression.getContent().toLowerCase().contains(rightTableViewTextField.getText().toLowerCase().trim()));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updateLeftTableViewDependingOnSelectionInRightTableView() {
        rightTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                leftTableView.getSelectionModel().clearSelection();
                if (toggleSwitch.isSelected()) {
                    try {
                        observableGroupList.clear();
                        observableGroupList.addAll(ConnectionManager.getGroupsFromExpression(newSelection));
                        if ("Name".equals(leftTableViewChoiceBox.getValue())) {
                            filteredGroupList.setPredicate(group -> group.getName().toLowerCase().contains(leftTableViewTextField.getText().toLowerCase().trim()));
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void listenToToggleSwitchAndUpdateTableView() {
        toggleSwitch.selectedProperty().addListener((observableValue, oldSelection, newSelection) -> {
            updateTableViewDependingOnToggleSwitch(newSelection);
        });
    }

    private void updateTableViewDependingOnToggleSwitch(Boolean expressionIsSwitchedOn) {
        leftTableViewTextField.setText("");
        rightTableViewTextField.setText("");
        observableGroupList.clear();
        leftTableView.getSelectionModel().clearSelection();
        observableExpressionList.clear();
        rightTableView.getSelectionModel().clearSelection();

        if (!expressionIsSwitchedOn) {
            try {
                observableGroupList.addAll(ConnectionManager.getAllGroups());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                observableExpressionList.addAll(ConnectionManager.getAllExpressions());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onDarkModeCheckMenuClick() {
        if (darkMode.isSelected()) {
            menuBar.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        } else {
            menuBar.getScene().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
    }

    @FXML
    private void onDeleteButtonClick() throws SQLException, ClassNotFoundException {
        if (!leftTableView.getSelectionModel().isEmpty()) {
            Group groupToDelete = leftTableView.getSelectionModel().getSelectedItem();
            ConnectionManager.deleteGroup(groupToDelete);
            observableGroupList.remove(groupToDelete);
            if (!toggleSwitch.isSelected()) {
                observableExpressionList.clear();
            }
        }
        if (!rightTableView.getSelectionModel().isEmpty()) {
            Expression expressionToDelete = rightTableView.getSelectionModel().getSelectedItem();
            ConnectionManager.deleteExpression(expressionToDelete);
            observableExpressionList.remove(expressionToDelete);
            if (toggleSwitch.isSelected()) {
                observableGroupList.clear();
            }
        }
    }

    @FXML
    private void onCreateNewGroupMenuItemClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createGroup.fxml"));
        Parent root = fxmlLoader.load();
        CreateGroupController createGroupController = fxmlLoader.getController();
        createGroupController.initializeNeedToReloadDataBooleanProperty(needToReloadData);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((MenuItem)event.getTarget()).getParentMenu().getParentPopup().getOwnerWindow());
        Scene scene = new Scene(root, 783, 440);
        if (darkMode.isSelected()) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
        stage.setTitle("Neue Gruppe Erstellen");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("book-icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onCreateNewExpressionMenuItemClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createExpression.fxml"));
        Parent root = fxmlLoader.load();
        CreateExpressionController createExpressionController = fxmlLoader.getController();
        createExpressionController.initializeNeedToReloadDataBooleanProperty(needToReloadData);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((MenuItem)event.getTarget()).getParentMenu().getParentPopup().getOwnerWindow());
        Scene scene = new Scene(root, 783, 440);
        if (darkMode.isSelected()) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
        stage.setTitle("Neues Wort Erstellen");
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

    @FXML
    private void onAboutMenuItemClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about.fxml"));
        Parent root = fxmlLoader.load();
        AboutController aboutController = fxmlLoader.getController();
        aboutController.setDarkMode(darkMode.isSelected());
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(menuBar.getScene().getWindow());
        Scene scene = new Scene(root, 600, 400);
        if (darkMode.isSelected()) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
        stage.setTitle("About");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("book-icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
