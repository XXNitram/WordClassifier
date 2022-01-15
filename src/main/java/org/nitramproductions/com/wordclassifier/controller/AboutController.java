package org.nitramproductions.com.wordclassifier.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class AboutController {

    @FXML
    private ImageView imageView;

    public AboutController() {

    }

    @FXML
    private void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("slow-zoom-nod.gif")));
        imageView.setImage(image);
    }
}
