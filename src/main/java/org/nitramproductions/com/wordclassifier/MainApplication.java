package org.nitramproductions.com.wordclassifier;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nitramproductions.com.wordclassifier.controller.MainController;
import org.nitramproductions.com.wordclassifier.controller.helper.AlertHelper;

import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;

public class MainApplication extends Application {

    private static Stage primaryStage;
    private static final AlertHelper alertHelper = new AlertHelper();

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Thread.setDefaultUncaughtExceptionHandler(MainApplication::showError);

        Preferences preferences = Preferences.userRoot().node("/wordclassifier");
        double stageWidth = preferences.getDouble("WINDOW_WIDTH", 800);
        double stageHeight = preferences.getDouble("WINDOW_HEIGHT", 600);
        double stagePositionX = preferences.getDouble("WINDOW_POSITION_X", 800);
        double stagePositionY= preferences.getDouble("WINDOW_POSITION_Y", 400);
        boolean stageMaximized = preferences.getBoolean("WINDOW_MAXIMIZED", false);
        boolean darkMode = preferences.getBoolean("DARK_MODE", false);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("controller/main.fxml"));
        fxmlLoader.setControllerFactory(controller -> new MainController(stage));
        Scene scene = new Scene(fxmlLoader.load());
        if (darkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("controller/helper/darkMode.css")).toExternalForm());
        }
        stage.setScene(scene);
        stage.setTitle("WordClassifier");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("controller/helper/book-icon.png"))));
        stage.setMaximized(stageMaximized);
        stage.setMinWidth(680);
        stage.setMinHeight(300);
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setX(stagePositionX);
        stage.setY(stagePositionY);
        stage.show();
    }

    private static void showError(Thread thread, Throwable throwable) {
        if (Platform.isFxApplicationThread()) {
            showErrorAlert(throwable);
        } else {
            System.err.println("An unexpected error occurred in " + thread);
        }
    }

    private static void showErrorAlert(Throwable throwable) {
        String stacktrace = ExceptionUtils.getStackTrace(throwable);
        TextArea textArea = new TextArea(stacktrace);
        textArea.setPrefSize(500, 200);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        Alert alert = alertHelper.createNewErrorAlert("Es ist ein Fehler aufgetreten!", primaryStage);
        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().expandedProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection) {
                alert.setResizable(false);
            }
        });
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}