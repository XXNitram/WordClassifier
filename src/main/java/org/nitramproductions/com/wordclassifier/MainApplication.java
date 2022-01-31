package org.nitramproductions.com.wordclassifier;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.nitramproductions.com.wordclassifier.controller.MainController;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(MainApplication::showError);
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("controller/main.fxml"));
        fxmlLoader.setControllerFactory(controller -> new MainController(stage));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("WordClassifier");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("controller/book-icon.png"))));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    private static void showError(Thread t, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            showErrorAlert();
        } else {
            System.err.println("An unexpected error occurred in " + t);
        }
    }

    private static void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Es ist ein Fehler aufgetreten!");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}