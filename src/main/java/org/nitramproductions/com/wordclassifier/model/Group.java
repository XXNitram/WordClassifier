package org.nitramproductions.com.wordclassifier.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class Group {

    private final StringProperty name;
    private final ObjectProperty<LocalDateTime> dateModified;
    private final ObjectProperty<LocalDateTime> creationDate;
    private StringProperty formattedDateModified;
    private StringProperty formattedCreationDate;

    public Group() {
        this(null, null, null);
    }

    public Group(String name) {
        this(name, null, null);
    }

    public Group(String name, LocalDateTime dateModified, LocalDateTime creationDate) {
        this.name = new SimpleStringProperty(name);
        this.dateModified = new SimpleObjectProperty<>(dateModified);
        setFormattedDateModified(dateModified);
        this.creationDate = new SimpleObjectProperty<>(creationDate);
        setFormattedCreationDate(creationDate);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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
        Group group = (Group) o;
        return Objects.equals(getName(), group.getName()) && Objects.equals(getDateModified(), group.getDateModified()) && Objects.equals(getFormattedDateModified(), group.getFormattedDateModified());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDateModified(), getFormattedDateModified());
    }
}
