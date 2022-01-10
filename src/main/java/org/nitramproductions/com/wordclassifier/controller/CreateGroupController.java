package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import org.nitramproductions.com.wordclassifier.MainApplication;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateGroupController {

    @FXML
    private ButtonBar buttonBar;

    @FXML
    private TextField newNameTextField;

    @FXML
    private TableView<Expression> leftTableView;
    @FXML
    private TableColumn<Expression, String> leftTableViewNameColumn;
    @FXML
    private TableView<Expression> rightTableView;
    @FXML
    private TableColumn<Expression, String> rightTableViewNameColumn;

    private ObservableList<Expression> leftList;
    private ObservableList<Expression> rightList;
    private List<Group> groupList;

    private final Validator validator = new Validator();

    private final Button createNewButton = new Button("Erstellen");
    private final Button cancelButton = new Button("Abbrechen");
    private final TooltipWrapper<Button> createNewWrapper = new TooltipWrapper<>(
            createNewButton,
            validator.containsErrorsProperty(),
            Bindings.concat("Gruppe kann nicht erstellt werden:\n", validator.createStringBinding())
    );

    private final ConnectionManager connectionManager = new ConnectionManager();
    private BooleanProperty needToReloadData;

    public CreateGroupController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        groupList = connectionManager.getAllGroups();
        leftList = FXCollections.observableArrayList(connectionManager.getAllExpressions());
        rightList = FXCollections.observableArrayList();
        leftTableView.setItems(leftList);
        rightTableView.setItems(rightList);

        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> onCancelButtonClick());
        cancelButton.translateXProperty().set(-20);
        createNewButton.setDefaultButton(true);
        createNewButton.setOnAction(e -> onCreateNewButtonClick());
        createNewButton.translateXProperty().set(-25);
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);

        validateNewNameTextField();
        deselectLeftIfRightSelected();
        deselectRightIfLeftSelected();

        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
    }

    public void initializeNeedToReloadDataBooleanProperty(BooleanProperty needToReloadData) {
        this.needToReloadData = needToReloadData;
    }

    private void validateNewNameTextField() {
        validator.createCheck()
                .dependsOn("newGroupName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newGroupName = c.get("newGroupName");
                    if (newGroupName.trim().isEmpty()) {
                        c.error("Bitte gib einen Namen ein!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();

        validator.createCheck()
                .dependsOn("newGroupName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newGroupName = c.get("newGroupName");
                    Pattern pattern = Pattern.compile("[^\\p{P}\\s+a-zA-Z0-9äöüÄÖÜß]");
                    Matcher matcher = pattern.matcher(newGroupName.trim());
                    if (matcher.find()) {
                        c.error("Es sind keine Sonderzeichen außer !\"#$%&'()*+,-./:;=<>?@[\\]^_`{|}~ erlaubt!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();

        validator.createCheck()
                .dependsOn("newGroupName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newGroupName = c.get("newGroupName");
                    if (newGroupName.trim().length() > 511) {
                        c.error("Der Name ist zu lang!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();

        validator.createCheck()
                .dependsOn("newGroupName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newGroupName = c.get("newGroupName");
                    if (!groupList.isEmpty()) {
                        for (Group group : groupList) {
                            if (newGroupName.trim().equalsIgnoreCase(group.getName())) {
                                c.error("Diese Gruppe existiert bereits!");
                            }
                        }
                    }
                })
                .decorates(newNameTextField)
                .immediate();
    }

    private void deselectRightIfLeftSelected() {
        leftTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Expression> change) -> {
                if (!leftTableView.getSelectionModel().getSelectedItems().isEmpty()) {
                    rightTableView.getSelectionModel().clearSelection();
                }
        });
    }

    private void deselectLeftIfRightSelected() {
        rightTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Expression> change) -> {
            if (!rightTableView.getSelectionModel().getSelectedItems().isEmpty()) {
                leftTableView.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML
    private void onRightArrowButtonClick() {
        ObservableList<Expression> selectedExpression = leftTableView.getSelectionModel().getSelectedItems();
        if (!selectedExpression.isEmpty()) {
            rightList.addAll(selectedExpression);
            leftList.removeAll(selectedExpression);
            leftTableView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onLeftArrowButtonClick() {
        ObservableList<Expression> selectedExpression = rightTableView.getSelectionModel().getSelectedItems();
        if (!selectedExpression.isEmpty()) {
            leftList.addAll(selectedExpression);
            rightList.removeAll(selectedExpression);
            rightTableView.getSelectionModel().clearSelection();
        }
    }

    private void onCreateNewButtonClick() {
        String newGroupName = newNameTextField.getText().trim();
        Group newGroup = new Group(newGroupName);
        try {
            connectionManager.addNewGroup(newGroup);
            if (!rightList.isEmpty()) {
                for (Expression expression : rightList) {
                    connectionManager.addNewBelongToRelation(newGroup, expression);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        needToReloadData.set(true);
        Stage stage = (Stage) createNewButton.getScene().getWindow();
        stage.close();
    }

    private void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
