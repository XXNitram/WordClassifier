package org.nitramproductions.com.wordclassifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.database.ConnectionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {
        ConnectionManager.initialize();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("controller/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 820, 600);
        stage.setTitle("WordClassifier");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("controller/book-icon.png"))));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    public static void main(String[] args) {
        launch();
    }
}