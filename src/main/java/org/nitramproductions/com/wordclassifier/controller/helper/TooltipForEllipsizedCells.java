package org.nitramproductions.com.wordclassifier.controller.helper;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

public class TooltipForEllipsizedCells<T> extends TableCell<T, String> {

    private BooleanProperty itemTextLongerThanColumnWidthProperty;

    public TooltipForEllipsizedCells() {

    }

    @Override
    protected void updateItem(final String item, final boolean empty) {
        super.updateItem(item, empty);
        setText(item);
        TableColumn<T, String> tableColumn = getTableColumn();
        double itemTextWidth = new Text(item).getLayoutBounds().getWidth();
        boolean itemTextLongerThanColumnWidth = tableColumn.getWidth() < itemTextWidth;
        itemTextLongerThanColumnWidthProperty = new SimpleBooleanProperty(itemTextLongerThanColumnWidth);
        tableColumn.widthProperty().addListener((observableValue, oldSelection, newSelection) ->
                itemTextLongerThanColumnWidthProperty.set(tableColumn.widthProperty().get() < itemTextWidth));
        tooltipProperty().bind(Bindings
                .when(Bindings
                        .and(itemTextLongerThanColumnWidthProperty, Bindings
                                .or(emptyProperty().not(), itemProperty().isNotNull())))
                .then(new CustomTooltip(item))
                .otherwise((CustomTooltip) null));
    }
}
