package org.nitramproductions.com.wordclassifier.controller;

import javafx.application.Platform;
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
import java.util.List;
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
    private SplitPane splitPane;
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

    private double stageWidth;
    private double stageHeight;
    private double stageXPosition;
    private double stageYPosition;
    private boolean isMaximized = false;
    private double oldStageXPosition;
    private double oldStageYPosition;

    double leftTableViewColumnDelta;
    double rightTableViewColumnDelta;

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
        initializeSplitPaneDividerPosition();
        initializeTableViewDependingOnToggleSwitch();
        initializeStageSizeAndPositionListeners();

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
        initializeLeftTableViewColumns();
        initializeRightTableViewColumns();
    }

    private void initializeLeftTableViewColumns() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        leftTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        leftTableViewNameColumn.setReorderable(false);
        leftTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().formattedDateModifiedProperty());
        leftTableViewDateModifiedColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        leftTableViewDateModifiedColumn.setReorderable(false);
        leftTableViewColumnDelta = preferences.getDouble("LEFT_TABLE_VIEW_NAME_COLUMN_OFFSET_FROM_CENTER", 0);
        Platform.runLater(() -> leftTableView.resizeColumn(leftTableViewNameColumn, leftTableViewColumnDelta));
    }

    private void initializeRightTableViewColumns() {
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        rightTableViewNameColumn.setReorderable(false);
        rightTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().formattedDateModifiedProperty());
        rightTableViewDateModifiedColumn.setCellFactory(column -> new TooltipForEllipsizedCells<>());
        rightTableViewDateModifiedColumn.setReorderable(false);
        rightTableViewColumnDelta = preferences.getDouble("RIGHT_TABLE_VIEW_NAME_COLUMN_OFFSET_FROM_CENTER", 0);
        Platform.runLater(() -> rightTableView.resizeColumn(rightTableViewNameColumn, rightTableViewColumnDelta));
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

    private void initializeSplitPaneDividerPosition() {
        double dividerPositionPref = preferences.getDouble("SPLIT_PANE_DIVIDER_POSITION", 0.5);
        splitPane.setDividerPosition(0, dividerPositionPref);
    }

    private void initializeTableViewDependingOnToggleSwitch() {
        boolean toggleSwitchSelectedPref = preferences.getBoolean("TOGGLE_SWITCH_SELECTED", false);
        Platform.runLater(() -> updateTableViewDependingOnToggleSwitch(toggleSwitchSelectedPref));
        toggleSwitch.setSelected(toggleSwitchSelectedPref);
    }

    private void initializeStageSizeAndPositionListeners() {
        mainStage.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!isMaximized) {
                stageWidth = newValue.doubleValue();
            }
        });

        mainStage.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!isMaximized) {
                stageHeight = newValue.doubleValue();
            }
        });

        mainStage.xProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!isMaximized) {
                stageXPosition = newValue.doubleValue();
                oldStageXPosition = oldValue.doubleValue();
            }
        });

        mainStage.yProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!isMaximized) {
                stageYPosition = newValue.doubleValue();
                oldStageYPosition = oldValue.doubleValue();
            }
        });

        mainStage.maximizedProperty().addListener((observableValue, oldValue, newValue) -> {
            isMaximized = newValue;
            if (isMaximized) {
                stageXPosition = oldStageXPosition;
                stageYPosition = oldStageYPosition;
            }
        });
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
                    List<Expression> belongingExpressions = connectionManager.getExpressionsBelongingToGroup(newSelection);
                    if (belongingExpressions.isEmpty()) {
                        rightTableView.setPlaceholder(new Label("Diese Gruppe hat keine zugeordneten Wörter!"));
                        updateRightTableViewColumnDelta();
                        rightTableViewNameColumn.setVisible(false);
                        rightTableViewDateModifiedColumn.setVisible(false);
                        return;
                    }
                    if(!rightTableViewNameColumn.isVisible()) {
                        rightTableViewNameColumn.setVisible(true);
                        if (expressionDateModifiedColumnCheckMenuItem.isSelected()) {
                            rightTableViewDateModifiedColumn.setVisible(true);
                        }
                        rightTableView.resizeColumn(rightTableViewNameColumn, rightTableViewColumnDelta);
                    }
                    observableExpressionList.addAll(belongingExpressions);
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
                    List<Group> belongingGroups = connectionManager.getGroupsBelongingToExpression(newSelection);
                    if (belongingGroups.isEmpty()) {
                        leftTableView.setPlaceholder(new Label("Dieses Wort ist zu keiner Gruppe zugeordnet!"));
                        updateLeftTableViewColumnDelta();
                        leftTableViewNameColumn.setVisible(false);
                        leftTableViewDateModifiedColumn.setVisible(false);
                        return;
                    }
                    if (!leftTableViewNameColumn.isVisible()) {
                        leftTableViewNameColumn.setVisible(true);
                        if (groupDateModifiedColumnCheckMenuItem.isSelected()) {
                            leftTableViewDateModifiedColumn.setVisible(true);
                        }
                        leftTableView.resizeColumn(leftTableViewNameColumn, leftTableViewColumnDelta);
                    }
                    observableGroupList.addAll(belongingGroups);
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
            if (!leftTableViewNameColumn.isVisible()) {
                leftTableViewNameColumn.setVisible(true);
                if (groupDateModifiedColumnCheckMenuItem.isSelected()) {
                    leftTableViewDateModifiedColumn.setVisible(true);
                }
                leftTableView.resizeColumn(leftTableViewNameColumn, leftTableViewColumnDelta);
            }
            rightTableView.setPlaceholder(new Label("Bitte Gruppe auswählen um zugeordnete Wörter zu sehen!"));
            updateRightTableViewColumnDelta();
            rightTableViewNameColumn.setVisible(false);
            rightTableViewDateModifiedColumn.setVisible(false);
            try {
                observableGroupList.addAll(connectionManager.getAllGroups());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            if (!rightTableViewNameColumn.isVisible()) {
                rightTableViewNameColumn.setVisible(true);
                if (expressionDateModifiedColumnCheckMenuItem.isSelected()) {
                    rightTableViewDateModifiedColumn.setVisible(true);
                }
                rightTableView.resizeColumn(rightTableViewNameColumn, rightTableViewColumnDelta);
            }
            leftTableView.setPlaceholder(new Label("Bitte Wort auswählen um zugeordnete Gruppen zu sehen!"));
            updateLeftTableViewColumnDelta();
            leftTableViewNameColumn.setVisible(false);
            leftTableViewDateModifiedColumn.setVisible(false);
            try {
                observableExpressionList.addAll(connectionManager.getAllExpressions());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLeftTableViewColumnDelta() {
        leftTableViewColumnDelta = leftTableViewNameColumn.getWidth() - (leftTableView.getWidth() / 2);
    }

    private void updateRightTableViewColumnDelta() {
        rightTableViewColumnDelta = rightTableViewNameColumn.getWidth() - (rightTableView.getWidth() / 2);
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
    private void onGroupDateModifiedColumnCheckMenuItemClick() {
        leftTableViewDateModifiedColumn.setVisible(groupDateModifiedColumnCheckMenuItem.isSelected());
        if (groupDateModifiedColumnCheckMenuItem.isSelected()) {
            resetLeftTableViewColumns();
        }
    }

    @FXML
    private void onExpressionDateModifiedColumnCheckMenuItemClick() {
        rightTableViewDateModifiedColumn.setVisible(expressionDateModifiedColumnCheckMenuItem.isSelected());
        if (expressionDateModifiedColumnCheckMenuItem.isSelected()) {
            resetRightTableViewColumns();
        }
    }

    @FXML
    private void onResetUIMenuItemClick() {
        splitPane.setDividerPositions(0.5);
        if (!groupDateModifiedColumnCheckMenuItem.isSelected()) {
            leftTableViewDateModifiedColumn.setVisible(true);
            groupDateModifiedColumnCheckMenuItem.setSelected(true);
        }
        if (!leftTableViewNameColumn.isVisible()) {
            leftTableViewNameColumn.setVisible(true);
            leftTableViewDateModifiedColumn.setVisible(true);
            resetLeftTableViewColumns();
            updateLeftTableViewColumnDelta();
            leftTableViewNameColumn.setVisible(false);
            leftTableViewDateModifiedColumn.setVisible(false);
        }
        resetLeftTableViewColumns();
        if (!expressionDateModifiedColumnCheckMenuItem.isSelected()) {
            rightTableViewDateModifiedColumn.setVisible(true);
            expressionDateModifiedColumnCheckMenuItem.setSelected(true);
        }
        if (!rightTableViewNameColumn.isVisible()) {
            rightTableViewNameColumn.setVisible(true);
            rightTableViewDateModifiedColumn.setVisible(true);
            resetRightTableViewColumns();
            updateRightTableViewColumnDelta();
            rightTableViewNameColumn.setVisible(false);
            rightTableViewDateModifiedColumn.setVisible(false);
        }
        resetRightTableViewColumns();
    }

    private void resetLeftTableViewColumns() {
        double leftTableViewColumnDeltaReset = (leftTableView.getWidth() / 2) - leftTableViewNameColumn.getWidth();
        leftTableView.resizeColumn(leftTableViewNameColumn, leftTableViewColumnDeltaReset);
    }

    private void resetRightTableViewColumns() {
        double rightTableViewColumnDeltaReset = (rightTableView.getWidth() / 2) - rightTableViewNameColumn.getWidth();
        rightTableView.resizeColumn(rightTableViewNameColumn, rightTableViewColumnDeltaReset);
    }

    @FXML
    private void onDarkModeCheckMenuItemClick() {
        if (darkModeCheckMenuItem.isSelected()) {
            mainStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("helper/darkMode.css")).toExternalForm());
        } else {
            mainStage.getScene().getStylesheets().remove(Objects.requireNonNull(getClass().getResource("helper/darkMode.css")).toExternalForm());
        }
        preferences.putBoolean("DARK_MODE", darkModeCheckMenuItem.isSelected());
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

    private void createNewStage(Parent root, String title, double width, double height) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);
        Scene scene = new Scene(root, width, height);
        if (darkModeCheckMenuItem.isSelected()) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("helper/darkMode.css")).toExternalForm());
        }
        stage.setX((mainStage.getX() + (mainStage.getWidth() / 2)) - (width / 2));
        stage.setY((mainStage.getY() + (mainStage.getHeight() / 2)) - (height / 2));
        stage.setTitle(title);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("helper/book-icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onCloseMenuItemClick() {
        throw new RuntimeException();
    }

    private void setPreferencesOnClose() {
        mainStage.setOnCloseRequest(event -> setPreferences());
    }

    private void setPreferences() {
        preferences.putDouble("WINDOW_WIDTH", stageWidth);
        preferences.putDouble("WINDOW_HEIGHT", stageHeight);
        preferences.putDouble("WINDOW_POSITION_X", stageXPosition);
        preferences.putDouble("WINDOW_POSITION_Y", stageYPosition);
        preferences.putBoolean("WINDOW_MAXIMIZED", mainStage.isMaximized());

        preferences.putBoolean("GROUP_MODIFICATION_DATE_COLUMN_ENABLED", groupDateModifiedColumnCheckMenuItem.isSelected());
        preferences.putBoolean("EXPRESSION_MODIFICATION_DATE_COLUMN_ENABLED", expressionDateModifiedColumnCheckMenuItem.isSelected());
        preferences.putDouble("SPLIT_PANE_DIVIDER_POSITION", splitPane.getDividerPositions()[0]);
        preferences.putBoolean("DARK_MODE", darkModeCheckMenuItem.isSelected());

        updateLeftTableViewColumnDelta();
        updateRightTableViewColumnDelta();
        preferences.putDouble("LEFT_TABLE_VIEW_NAME_COLUMN_OFFSET_FROM_CENTER", leftTableViewColumnDelta);
        preferences.putDouble("RIGHT_TABLE_VIEW_NAME_COLUMN_OFFSET_FROM_CENTER", rightTableViewColumnDelta);

        preferences.putBoolean("TOGGLE_SWITCH_SELECTED", toggleSwitch.isSelected());
    }
}
