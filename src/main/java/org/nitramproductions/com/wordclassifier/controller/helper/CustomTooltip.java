package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.scene.control.Tooltip;

public class CustomTooltip extends Tooltip {

    public CustomTooltip(String text) {
        super(text);
        setWrapText(true);
        setPrefWidth(250);
    }
}
