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

public class CreateExpressionController {

    @FXML
    private ButtonBar buttonBar;

    @FXML
    private TextField newNameTextField;

    @FXML
    private TableView<Group> leftTableView;
    @FXML
    private TableColumn<Group, String> leftTableViewNameColumn;
    @FXML
    private TableView<Group> rightTableView;
    @FXML
    private TableColumn<Group, String> rightTableViewNameColumn;

    private ObservableList<Group> leftList;
    private ObservableList<Group> rightList;
    private List<Expression> expressionList;

    private final Validator validator = new Validator();

    private final Button createNewButton = new Button("Erstellen");
    private final Button cancelButton = new Button("Abbrechen");
    private final TooltipWrapper<Button> createNewWrapper = new TooltipWrapper<>(
            createNewButton,
            validator.containsErrorsProperty(),
            Bindings.concat("Wort kann nicht erstellt werden:\n", validator.createStringBinding())
    );

    private BooleanProperty needToReloadData;

    public CreateExpressionController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        expressionList = ConnectionManager.getAllExpressions();
        leftList = FXCollections.observableArrayList(ConnectionManager.getAllGroups());
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

        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }

    public void initializeNeedToReloadDataBooleanProperty(BooleanProperty needToReloadData) {
        this.needToReloadData = needToReloadData;
    }

    private void validateNewNameTextField() {
        validator.createCheck()
                .dependsOn("newExpressionName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newExpressionName = c.get("newExpressionName");
                    if (newExpressionName.trim().isEmpty()) {
                        c.error("Bitte gib einen Namen ein!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();

        validator.createCheck()
                .dependsOn("newExpressionName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newExpressionName = c.get("newExpressionName");
                    Pattern pattern = Pattern.compile("[^\\p{P}\\s+a-zA-Z0-9äöüÄÖÜß]");
                    Matcher matcher = pattern.matcher(newExpressionName.trim());
                    if (matcher.find()) {
                        c.error("Es sind keine Sonderzeichen außer !\"#$%&'()*+,-./:;=<>?@[\\]^_`{|}~ erlaubt!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();

        validator.createCheck()
                .dependsOn("newExpressionName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newExpressionName = c.get("newExpressionName");
                    if (newExpressionName.trim().length() > 511) {
                        c.error("Der Name ist zu lang!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();

        validator.createCheck()
                .dependsOn("newExpressionName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newExpressionName = c.get("newExpressionName");
                    if (!expressionList.isEmpty()) {
                        for (Expression expression : expressionList) {
                            if (newExpressionName.trim().equalsIgnoreCase(expression.getContent())) {
                                c.error("Dieses Wort existiert bereits!");
                            }
                        }
                    }
                })
                .decorates(newNameTextField)
                .immediate();
    }

    private void deselectRightIfLeftSelected() {
        leftTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Group> change) -> {
                if (!leftTableView.getSelectionModel().getSelectedItems().isEmpty()) {
                    rightTableView.getSelectionModel().clearSelection();
                }
        });
    }

    private void deselectLeftIfRightSelected() {
        rightTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Group> change) -> {
            if (!rightTableView.getSelectionModel().getSelectedItems().isEmpty()) {
                leftTableView.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML
    private void onRightArrowButtonClick() {
        ObservableList<Group> selectedGroups = leftTableView.getSelectionModel().getSelectedItems();
        if (!selectedGroups.isEmpty()) {
            rightList.addAll(selectedGroups);
            leftList.removeAll(selectedGroups);
            leftTableView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onLeftArrowButtonClick() {
        ObservableList<Group> selectedGroups = rightTableView.getSelectionModel().getSelectedItems();
        if (!selectedGroups.isEmpty()) {
            leftList.addAll(selectedGroups);
            rightList.removeAll(selectedGroups);
            rightTableView.getSelectionModel().clearSelection();
        }
    }

    private void onCreateNewButtonClick() {
        String newExpressionName = newNameTextField.getText().trim();
        Expression newExpression = new Expression(newExpressionName);
        try {
            ConnectionManager.addNewExpression(newExpression);
            if (!rightList.isEmpty()) {
                for (Group group : rightList) {
                    ConnectionManager.addNewBelongToRelation(group, newExpression);
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
