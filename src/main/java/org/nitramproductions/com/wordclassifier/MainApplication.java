package org.nitramproductions.com.wordclassifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;

import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {
        ConnectionManager.initialize();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("controller/main.fxml"));
        // TODO Set minimum stage size
        Scene scene = new Scene(fxmlLoader.load(), 1040, 585);
        stage.setTitle("WordClassifier");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}