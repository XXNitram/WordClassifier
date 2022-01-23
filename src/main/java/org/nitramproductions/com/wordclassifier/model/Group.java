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
    private StringProperty formattedDateModified;

    public Group() {
        this(null, null);
    }

    public Group(String name) {
        this(name, null);
    }

    public Group(String name, LocalDateTime dateModified) {
        this.name = new SimpleStringProperty(name);
        this.dateModified = new SimpleObjectProperty<>(dateModified);
        localDatePropertyToStringProperty(dateModified);
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
        Group group = (Group) o;
        return Objects.equals(getName(), group.getName()) && Objects.equals(getDateModified(), group.getDateModified()) && Objects.equals(getFormattedDateModified(), group.getFormattedDateModified());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDateModified(), getFormattedDateModified());
    }
}
