package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationHelper {

    public AnimationHelper() {

    }

    public FadeTransition setUpFadeInTransition(Node node, Duration duration) {
        FadeTransition fadeIn = new FadeTransition(duration);
        fadeIn.setNode(node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
        return fadeIn;
    }

    public FadeTransition setUpFadeOutTransition(Node node, Duration duration) {
        FadeTransition fadeOut = new FadeTransition(duration);
        fadeOut.setNode(node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setCycleCount(1);
        fadeOut.setAutoReverse(false);
        return fadeOut;
    }
}
