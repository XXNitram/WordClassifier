package org.nitramproductions.com.wordclassifier.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.IOException;
import java.time.LocalDateTime;

public class MainController {

    @FXML
    private TableView<Group> leftTableView;
    @FXML
    private TableColumn<Group, String> leftTableViewNameColumn;
    @FXML
    private TableColumn<Group, LocalDateTime> leftTableViewDateModifiedColumn;

    public MainController() {

    }

    @FXML
    private void initialize() {
        leftTableViewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        leftTableViewDateModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().dateModifiedProperty());
    }

    @FXML
    protected void onCreateNewMenuItemClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((MenuItem)event.getTarget()).getParentPopup().getOwnerWindow());
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setLeftTableViewData(ObservableList<Group> observableList) {
        leftTableView.setItems(observableList);
    }
}
