package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.nitramproductions.com.wordclassifier.controller.helper.SearchHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.SelectionHelper;
import org.nitramproductions.com.wordclassifier.controller.helper.TooltipForEllipsizedCells;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.prefs.Preferences;

public class MainController {

    @FXML
    private TableView<Group> leftTableView;
    @FXML
    private TableColumn<Group, String> leftTableViewNameColumn;
    @FXML
    private TableColumn<Group, String> leftTableViewDateModifiedColumn;
    @FXML
    private TableView<Expression> rightTableView;
    @FXML
    private TableColumn<Expression, String> rightTableViewNameColumn;
    @FXML
    private TableColumn<Expression, String> rightTableViewDateModifiedColumn;
    @FXML
    private ChoiceBox<String> leftTableViewChoiceBox;
    @FXML
    private ChoiceBox<String> rightTableViewChoiceBox;
    @FXML
    private TextField leftTableViewSearchTextField;
    @FXML
    private TextField rightTableViewSearchTextField;
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
    private final Stage stage;
    private final Preferences preferences;

    public MainController(Stage stage) {
        this.stage = stage;
        preferences = Preferences.userRoot().node("/wordclassifier");
    }

    @FXML
    private void initialize() throws SQLException, IOException {
        connectionManager.initialize();
        initializeGroupLists();
        initializeExpressionLists();
        initializeTableViews();
        initializeTableViewColumns();
        initializeChoiceBoxes();
        initializeKeyboardShortcuts();
        initializeDarkModeCheckMenuItem();

        setPreferencesOnClose();

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

    private void initializeTableViews() {
        leftTableView.setRowFactory(tableView -> {
            TableRow<Group> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    try {
                        openEditGroupView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });
        rightTableView.setRowFactory(tableView -> {
            TableRow<Expression> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    try {
                        openEditExpressionView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

    private void initializeTableViewColumns() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        leftTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        leftTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().formattedDateModifiedProperty());
        leftTableViewDateModifiedColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());

        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        rightTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().formattedDateModifiedProperty());
        rightTableViewDateModifiedColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
    }

    private void initializeChoiceBoxes() {
        leftTableViewChoiceBox.getItems().addAll("Name", "Änderungsdatum");
        leftTableViewChoiceBox.getSelectionModel().select("Name");
        rightTableViewChoiceBox.getItems().addAll("Name", "Änderungsdatum");
        rightTableViewChoiceBox.getSelectionModel().select("Name");
    }

    private void initializeKeyboardShortcuts() {
        KeyCombination keyCombinationEdit = new KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN);
        KeyCombination keyCombinationDelete = new KeyCodeCombination(KeyCode.DELETE);
        Runnable runnableEdit = () -> {
            try {
                onEditButtonClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Runnable runnableDelete = () -> {
            try {
                onDeleteButtonClick();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        menuBar.sceneProperty().addListener((observableValue, oldSelection, newSelection) -> {
            menuBar.getScene().getAccelerators().put(keyCombinationEdit, runnableEdit);
            menuBar.getScene().getAccelerators().put(keyCombinationDelete, runnableDelete);
        });
    }

    private void initializeDarkModeCheckMenuItem() {
        boolean darkModePref = preferences.getBoolean("DARK_MODE", false);
        darkMode.setSelected(darkModePref);
    }

    private void searchLeftTableView() {
        searchHelper.searchFilteredGroupListDependingOnChoiceBox(leftTableViewSearchTextField, leftTableViewChoiceBox, filteredGroupList);
        searchHelper.clearTextFieldIfChoiceBoxChanged(leftTableViewChoiceBox, leftTableViewSearchTextField);
    }

    private void searchRightTableView() {
        searchHelper.searchFilteredExpressionListDependingOnChoiceBox(rightTableViewSearchTextField, rightTableViewChoiceBox, filteredExpressionList);
        searchHelper.clearTextFieldIfChoiceBoxChanged(rightTableViewChoiceBox, rightTableViewSearchTextField);
    }

    private void deselectListIfAnotherIsSelected() {
        selectionHelper.deselectEitherTableViewIfOtherGetsSelected(leftTableView, rightTableView);
    }

    private void updateRightTableViewDependingOnSelectionInLeftTableView() {
        leftTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null && !toggleSwitch.isSelected()) {
                try {
                    observableExpressionList.clear();
                    observableExpressionList.addAll(connectionManager.getExpressionsBelongingToGroup(newSelection));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateLeftTableViewDependingOnSelectionInRightTableView() {
        rightTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null && toggleSwitch.isSelected()) {
                try {
                    observableGroupList.clear();
                    observableGroupList.addAll(connectionManager.getGroupsBelongingToExpression(newSelection));
                } catch (SQLException e) {
                    e.printStackTrace();
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
        leftTableViewSearchTextField.setText("");
        rightTableViewSearchTextField.setText("");
        observableGroupList.clear();
        leftTableView.getSelectionModel().clearSelection();
        observableExpressionList.clear();
        rightTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void onEditButtonClick() throws IOException {
        if (!leftTableView.getSelectionModel().isEmpty()) {
            openEditGroupView();
        }
        if (!rightTableView.getSelectionModel().isEmpty()) {
            openEditExpressionView();
        }
    }

    private void openEditGroupView() throws IOException {
        Group groupToEdit = leftTableView.getSelectionModel().getSelectedItem();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editGroup.fxml"));
        fxmlLoader.setControllerFactory(controller -> new EditGroupController(groupToEdit, needToReloadData));
        Parent root = fxmlLoader.load();
        createNewStage(root, "Gruppe bearbeiten", 783, 440);
    }

    private void openEditExpressionView() throws IOException {
        Expression expressionToEdit = rightTableView.getSelectionModel().getSelectedItem();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editExpression.fxml"));
        fxmlLoader.setControllerFactory(controller -> new EditExpressionController(expressionToEdit, needToReloadData));
        Parent root = fxmlLoader.load();
        createNewStage(root, "Wort bearbeiten", 783, 440);
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
        if (darkMode.isSelected()) {
            stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        } else {
            stage.getScene().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
    }

    @FXML
    private void onCreateNewGroupMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createGroup.fxml"));
        Parent root = fxmlLoader.load();
        CreateGroupController createGroupController = fxmlLoader.getController();
        createGroupController.setNeedToReloadDataBooleanProperty(needToReloadData);
        createNewStage(root, "Neue Gruppe Erstellen", 783, 440);
    }

    @FXML
    private void onCreateNewExpressionMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createExpression.fxml"));
        Parent root = fxmlLoader.load();
        CreateExpressionController createExpressionController = fxmlLoader.getController();
        createExpressionController.setNeedToReloadDataBooleanProperty(needToReloadData);
        createNewStage(root, "Neues Wort Erstellen", 783, 440);
    }

    @FXML
    private void onImportCSVMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("importFromCSV.fxml"));
        fxmlLoader.setControllerFactory(controller -> new ImportFromCSVController(needToReloadData));
        Parent root = fxmlLoader.load();
        createNewStage(root, "Importieren", 350, 200);
    }

    @FXML
    private void onExportCSVMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("exportToCSV.fxml"));
        Parent root = fxmlLoader.load();
        createNewStage(root, "Exportieren", 350, 230);
    }

    @FXML
    private void onAboutMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about.fxml"));
        Parent root = fxmlLoader.load();
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
        setPreferences();
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    private void setPreferencesOnClose() {
        stage.setOnCloseRequest(event -> setPreferences());
    }

    private void setPreferences() {
        preferences.putBoolean("WINDOW_MAXIMIZED", stage.isMaximized());
        preferences.putDouble("WINDOW_WIDTH",stage.getWidth());
        preferences.putDouble("WINDOW_HEIGHT",stage.getHeight());
        preferences.putDouble("WINDOW_POSITION_X", stage.getX());
        preferences.putDouble("WINDOW_POSITION_Y", stage.getY());
        preferences.putBoolean("DARK_MODE", darkMode.isSelected());
    }
}
