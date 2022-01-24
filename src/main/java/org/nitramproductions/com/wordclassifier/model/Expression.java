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
    private final ObjectProperty<LocalDateTime> creationDate;
    private StringProperty formattedDateModified;
    private StringProperty formattedCreationDate;

    public Expression() {
        this(null, null, null);
    }

    public Expression(String name) {
        this(name, null, null);
    }

    public Expression(String content, LocalDateTime dateModified, LocalDateTime creationDate) {
        this.content = new SimpleStringProperty(content);
        this.dateModified = new SimpleObjectProperty<>(dateModified);
        setFormattedDateModified(dateModified);
        this.creationDate = new SimpleObjectProperty<>(creationDate);
        setFormattedCreationDate(creationDate);
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
        setFormattedDateModified(dateModified);
    }

    public LocalDateTime getCreationDate() {
        return creationDate.get();
    }

    public ObjectProperty<LocalDateTime> creationDateProperty() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate.set(creationDate);
        setFormattedCreationDate(creationDate);
    }

    public String getFormattedDateModified() {
        return formattedDateModified.get();
    }

    public StringProperty formattedDateModifiedProperty() {
        return formattedDateModified;
    }

    public String getFormattedCreationDate() {
        return formattedCreationDate.get();
    }

    public StringProperty formattedCreationDateProperty() {
        return formattedCreationDate;
    }

    private void setFormattedDateModified(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            formattedDateModified = new SimpleStringProperty(formatLocalDateTime(localDateTime));
        }
    }

    private void setFormattedCreationDate(LocalDateTime creationDate) {
        if (creationDate != null) {
            formattedCreationDate = new SimpleStringProperty(formatLocalDateTime(creationDate));
        }
    }

    private String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);
        return localDateTime.format(formatter);
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
