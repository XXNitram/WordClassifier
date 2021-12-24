package org.nitramproductions.com.wordclassifier;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.controller.MainController;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MainApplication extends Application {

    private final ObservableList<Group> groupData = FXCollections.observableArrayList();
    public static BooleanProperty needToReloadData = new SimpleBooleanProperty(false);

    public void addData() {
        groupData.add(new Group("Gruppe 1", LocalDateTime.of(1999, 2, 21, 22, 12)));
        groupData.add(new Group("Gruppe 2", LocalDateTime.of(2000, 2, 1, 12, 13)));
        groupData.add(new Group("Gruppe 3", LocalDateTime.of(1978, 6, 23, 2, 30)));
        groupData.add(new Group("Gruppe 4", LocalDateTime.of(1234, 4, 14, 3, 50)));
        groupData.add(new Group("Gruppe 5", LocalDateTime.of(1789, 12, 15, 23, 40)));
        groupData.add(new Group("Gruppe 6", LocalDateTime.of(2001, 9, 12, 14, 35)));
        groupData.add(new Group("Gruppe 7", LocalDateTime.of(1994, 5, 7, 18, 27)));
        groupData.add(new Group("Gruppe 8", LocalDateTime.of(1969, 1, 9, 8, 49)));
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {
        ConnectionManager.initialize();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("controller/main.fxml"));
        // TODO Set minimum stage size
        Scene scene = new Scene(fxmlLoader.load(), 1040, 585);
        MainController mainController = fxmlLoader.getController();
        //mainController.setLeftTableViewData(ConnectionManager.getAllGroups());
        stage.setTitle("WordClassifier");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}