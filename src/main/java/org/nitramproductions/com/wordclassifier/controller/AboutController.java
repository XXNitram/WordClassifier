package org.nitramproductions.com.wordclassifier.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.Objects;

public class AboutController {

    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane anchorPane;
    private Boolean darkMode = false;

    public AboutController() {

    }

    @FXML
    private void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("slow-zoom-nod.gif")));
        imageView.setImage(image);
        if (darkMode) {
            anchorPane.setStyle("-fx-background-color: #373e43");
        }
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }
}
