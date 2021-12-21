module org.nitramproductions.com.wordclassifier {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires com.h2database;

    opens org.nitramproductions.com.wordclassifier to javafx.fxml;
    exports org.nitramproductions.com.wordclassifier;
    exports org.nitramproductions.com.wordclassifier.controller;
    opens org.nitramproductions.com.wordclassifier.controller to javafx.fxml;
}