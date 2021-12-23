package org.nitramproductions.com.wordclassifier.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
    @FXML
    private Button rightArrowButton;
    @FXML
    private Button leftArrowButton;

    private ObservableList<Expression> leftList;
    private ObservableList<Expression> rightList;
    private Validator validator = new Validator();

    private Button createNewButton = new Button("Erstellen");
    private Button cancelButton = new Button("Abbrechen");
    private TooltipWrapper<Button> createNewWrapper = new TooltipWrapper<>(
            createNewButton,
            validator.containsErrorsProperty(),
            Bindings.concat("Gruppe kann nicht erstellt werden:\n", validator.createStringBinding())
    );

    public CreateGroupController() {

    }

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        leftList = ConnectionManager.getAllExpressions();
        rightList = FXCollections.observableArrayList();
        leftTableView.setItems(leftList);
        rightTableView.setItems(rightList);

        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        validateNewNameTextField();

        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> onCancelButtonClick());
        cancelButton.translateXProperty().set(-20);
        createNewButton.setDefaultButton(true);
        createNewButton.setOnAction(e -> onCreateNewButtonClick());
        createNewButton.translateXProperty().set(-25);
        buttonBar.getButtons().addAll(createNewWrapper, cancelButton);

        deselectLeftIfRightSelected();
        deselectRightIfLeftSelected();

        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        rightTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
    }

    private void validateNewNameTextField() {
        validator.createCheck()
                .dependsOn("newGroupName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newGroupName = c.get("newGroupName");
                    if (!(newGroupName.trim().length() > 0)) {
                        c.error("Bitte gib einen Namen ein!");
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
        ObservableList<Expression> selectedGroups = leftTableView.getSelectionModel().getSelectedItems();
        if (!selectedGroups.isEmpty()) {
            rightList.addAll(selectedGroups);
            leftList.removeAll(selectedGroups);
            leftTableView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onLeftArrowButtonClick() {
        ObservableList<Expression> selectedGroups = rightTableView.getSelectionModel().getSelectedItems();
        if (!selectedGroups.isEmpty()) {
            leftList.addAll(selectedGroups);
            rightList.removeAll(selectedGroups);
            rightTableView.getSelectionModel().clearSelection();
        }
    }

    private void onCreateNewButtonClick() {

    }

    private void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
