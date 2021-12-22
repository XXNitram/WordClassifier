package org.nitramproductions.com.wordclassifier.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Expression {

    private final StringProperty content;
    private final ObjectProperty<LocalDateTime> dateModified;

    public Expression() {
        this(null, null);
    }

    public Expression(StringProperty content, ObjectProperty<LocalDateTime> dateModified) {
        this.content = content;
        this.dateModified = dateModified;
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public LocalDateTime getDateModified() {
        return dateModified.get();
    }

    public ObjectProperty<LocalDateTime> dateModifiedProperty() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified.set(dateModified);
    }
}
