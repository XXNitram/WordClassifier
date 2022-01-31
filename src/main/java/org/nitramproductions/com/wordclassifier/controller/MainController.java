package org.nitramproductions.com.wordclassifier.controller;

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
    private CheckMenuItem darkModeCheckMenuItem;
    @FXML
    private CheckMenuItem groupDateModifiedColumnCheckMenuItem;
    @FXML
    private CheckMenuItem expressionDateModifiedColumnCheckMenuItem;
    @FXML
    private ToggleSwitch toggleSwitch;

    private ObservableList<Group> observableGroupList;
    private FilteredList<Group> filteredGroupList;
    private ObservableList<Expression> observableExpressionList;
    private FilteredList<Expression> filteredExpressionList;

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SearchHelper searchHelper = new SearchHelper();
    private final SelectionHelper selectionHelper = new SelectionHelper();
    private final Stage mainStage;
    private final Preferences preferences;

    public MainController(Stage mainStage) {
        this.mainStage = mainStage;
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
        initializeColumnCheckMenuItems();

        setPreferencesOnClose();

        searchLeftTableView();
        searchRightTableView();

        deselectListIfAnotherIsSelected();
        updateLeftTableViewDependingOnSelectionInRightTableView();
        updateRightTableViewDependingOnSelectionInLeftTableView();
        listenToToggleSwitchAndUpdateTableView();
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
        darkModeCheckMenuItem.setSelected(darkModePref);
    }

    private void initializeColumnCheckMenuItems() {
        boolean groupDateModifiedColumnEnabledPref = preferences.getBoolean("GROUP_MODIFICATION_DATE_COLUMN_ENABLED", true);
        boolean expressionDateModifiedEnabledPref = preferences.getBoolean("EXPRESSION_MODIFICATION_DATE_COLUMN_ENABLED", true);
        leftTableViewDateModifiedColumn.setVisible(groupDateModifiedColumnEnabledPref);
        rightTableViewDateModifiedColumn.setVisible(expressionDateModifiedEnabledPref);
        groupDateModifiedColumnCheckMenuItem.setSelected(groupDateModifiedColumnEnabledPref);
        expressionDateModifiedColumnCheckMenuItem.setSelected(expressionDateModifiedEnabledPref);
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

    public void reloadData() {
        updateTableViewDependingOnToggleSwitch(toggleSwitch.isSelected());
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
        fxmlLoader.setControllerFactory(controller -> new EditGroupController(this, groupToEdit));
        Parent root = fxmlLoader.load();
        createNewStage(root, "Gruppe bearbeiten", 783, 440);
    }

    private void openEditExpressionView() throws IOException {
        Expression expressionToEdit = rightTableView.getSelectionModel().getSelectedItem();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editExpression.fxml"));
        fxmlLoader.setControllerFactory(controller -> new EditExpressionController(this, expressionToEdit));
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
    private void onDarkModeCheckMenuItemClick() {
        if (darkModeCheckMenuItem.isSelected()) {
            mainStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        } else {
            mainStage.getScene().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("darkMode.css")).toExternalForm());
        }
    }

    @FXML
    private void onGroupDateModifiedColumnCheckMenuItemClick() {
        leftTableViewDateModifiedColumn.setVisible(groupDateModifiedColumnCheckMenuItem.isSelected());
    }

    @FXML
    private void onExpressionDateModifiedColumnCheckMenuItemClick() {
        rightTableViewDateModifiedColumn.setVisible(expressionDateModifiedColumnCheckMenuItem.isSelected());
    }

    @FXML
    private void onCreateNewGroupMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createGroup.fxml"));
        fxmlLoader.setControllerFactory(controller -> new CreateGroupController(this));
        Parent root = fxmlLoader.load();
        createNewStage(root, "Neue Gruppe Erstellen", 783, 440);
    }

    @FXML
    private void onCreateNewExpressionMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createExpression.fxml"));
        fxmlLoader.setControllerFactory(controller -> new CreateExpressionController(this));
        Parent root = fxmlLoader.load();
        createNewStage(root, "Neues Wort Erstellen", 783, 440);
    }

    @FXML
    private void onImportCSVMenuItemClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("importFromCSV.fxml"));
        fxmlLoader.setControllerFactory(controller -> new ImportFromCSVController(this));
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
        if (darkModeCheckMenuItem.isSelected()) {
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
        mainStage.close();
    }

    private void setPreferencesOnClose() {
        mainStage.setOnCloseRequest(event -> setPreferences());
    }

    private void setPreferences() {
        preferences.putBoolean("WINDOW_MAXIMIZED", mainStage.isMaximized());
        preferences.putDouble("WINDOW_WIDTH", mainStage.getWidth());
        preferences.putDouble("WINDOW_HEIGHT", mainStage.getHeight());
        preferences.putDouble("WINDOW_POSITION_X", mainStage.getX());
        preferences.putDouble("WINDOW_POSITION_Y", mainStage.getY());
        preferences.putBoolean("DARK_MODE", darkModeCheckMenuItem.isSelected());
        preferences.putBoolean("GROUP_MODIFICATION_DATE_COLUMN_ENABLED", groupDateModifiedColumnCheckMenuItem.isSelected());
        preferences.putBoolean("EXPRESSION_MODIFICATION_DATE_COLUMN_ENABLED", expressionDateModifiedColumnCheckMenuItem.isSelected());
    }
}
