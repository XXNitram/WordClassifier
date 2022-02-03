package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class TooltipForTextFieldHelper {

    public TooltipForTextFieldHelper() {

    }

    public void setTooltipForTextFieldIfContentExceedsWidth(TextField textField) {
        double itemTextWidth = new Text(textField.getText()).getLayoutBounds().getWidth();
        if (textField.getWidth() < itemTextWidth) {
            textField.setTooltip(new CustomTooltip(textField.getText()));
        } else {
            textField.setTooltip(null);
        }
    }

    public void setTooltipForTextFieldIfContentExceedsWidthAndListenToChanges(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) ->
                setTooltipForTextFieldIfContentExceedsWidth(textField));
    }

}
