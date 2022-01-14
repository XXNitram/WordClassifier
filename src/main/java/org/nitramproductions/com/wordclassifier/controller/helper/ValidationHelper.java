package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {

    private final Validator validator = new Validator();

    public ValidationHelper() {

    }

    public void checkIfEmpty(TextField newNameTextField) {
        validator.createCheck()
                .dependsOn("newName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newName = c.get("newName");
                    if (newName.trim().isEmpty()) {
                        c.error("Bitte gib einen Namen ein!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();
    }

    public void checkIfIncludesSpecialCharacter(TextField newNameTextField) {
        validator.createCheck()
                .dependsOn("newName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newName = c.get("newName");
                    Pattern pattern = Pattern.compile("[^\\p{P}\\s+a-zA-Z0-9äöüÄÖÜß]");
                    Matcher matcher = pattern.matcher(newName.trim());
                    if (matcher.find()) {
                        c.error("Es sind keine Sonderzeichen außer !\"#$%&'()*+,-./:;=<>?@[\\]^_`{|}~ erlaubt!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();
    }

    public void checkIfTooLong(TextField newNameTextField) {
        validator.createCheck()
                .dependsOn("newName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newName = c.get("newName");
                    if (newName.trim().length() > 511) {
                        c.error("Der Name ist zu lang!");
                    }
                })
                .decorates(newNameTextField)
                .immediate();
    }

    public void checkIfExpressionAlreadyExists(TextField newNameTextField, List<Expression> expressions) {
        validator.createCheck()
                .dependsOn("newExpressionName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newExpressionName = c.get("newExpressionName");
                    if (!expressions.isEmpty()) {
                        for (Expression expression : expressions) {
                            if (newExpressionName.trim().equalsIgnoreCase(expression.getContent())) {
                                c.error("Dieses Wort existiert bereits!");
                            }
                        }
                    }
                })
                .decorates(newNameTextField)
                .immediate();
    }

    public void checkIfGroupAlreadyExists(TextField newNameTextField, List<Group> groups) {
        validator.createCheck()
                .dependsOn("newGroupName", newNameTextField.textProperty())
                .withMethod(c -> {
                    String newGroupName = c.get("newGroupName");
                    if (!groups.isEmpty()) {
                        for (Group group : groups) {
                            if (newGroupName.trim().equalsIgnoreCase(group.getName())) {
                                c.error("Diese Gruppe existiert bereits!");
                            }
                        }
                    }
                })
                .decorates(newNameTextField)
                .immediate();
    }

    public TooltipWrapper<Button> getTooltipWrapper(Button button, String errorMessage) {
        return new TooltipWrapper<>(
                button,
                validator.containsErrorsProperty(),
                Bindings.concat(errorMessage + "\n", validator.createStringBinding())
        );
    }
}
