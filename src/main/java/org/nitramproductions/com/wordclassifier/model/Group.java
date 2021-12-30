package org.nitramproductions.com.wordclassifier.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Group {

     private final StringProperty name;
     private final ObjectProperty<LocalDateTime> dateModified;

     public Group() {
          this(null,null);
     }

     public Group(String name) {
          this(name, null);
     }

     public Group(String name, LocalDateTime dateModified) {
          this.name = new SimpleStringProperty(name);
          this.dateModified = new SimpleObjectProperty<>(dateModified);
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
     }
}
