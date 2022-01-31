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
import java.util.prefs.Preferences;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(MainApplication::showError);

        Preferences preferences = Preferences.userRoot().node("/wordclassifier");
        double stageWidth = preferences.getDouble("WINDOW_WIDTH", 800);
        double stageHeight = preferences.getDouble("WINDOW_HEIGHT", 600);
        boolean stageMaximized = preferences.getBoolean("WINDOW_MAXIMIZED", false);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("controller/main.fxml"));
        fxmlLoader.setControllerFactory(controller -> new MainController(stage));
        Scene scene = new Scene(fxmlLoader.load(), stageWidth, stageHeight);
        stage.setTitle("WordClassifier");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("controller/book-icon.png"))));
        stage.setMaximized(stageMaximized);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.setMinWidth(680);
        stage.setMinHeight(300);
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