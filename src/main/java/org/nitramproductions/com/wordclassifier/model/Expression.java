package org.nitramproductions.com.wordclassifier.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class Expression {

    private final StringProperty content;
    private final ObjectProperty<LocalDateTime> dateModified;
    private StringProperty formattedDateModified;

    public Expression() {
        this(null, null);
    }

    public Expression(String name) {
        this(name, null);
    }

    public Expression(String content, LocalDateTime dateModified) {
        this.content = new SimpleStringProperty(content);
        this.dateModified = new SimpleObjectProperty<>(dateModified);
        localDatePropertyToStringProperty(dateModified);
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
        localDatePropertyToStringProperty(dateModified);
    }

    public String getFormattedDateModified() {
        return formattedDateModified.get();
    }

    public StringProperty formattedDateModifiedProperty() {
        return formattedDateModified;
    }

    private void localDatePropertyToStringProperty(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);
            String newFormat = localDateTime.format(formatter);
            formattedDateModified = new SimpleStringProperty(newFormat);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expression that = (Expression) o;
        return Objects.equals(getContent(), that.getContent()) && Objects.equals(getDateModified(), that.getDateModified()) && Objects.equals(getFormattedDateModified(), that.getFormattedDateModified());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getDateModified(), getFormattedDateModified());
    }
}
