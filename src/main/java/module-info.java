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
    requires java.net.http;
    requires java.sql;
    requires java.desktop;
    requires java.prefs;
    requires com.h2database;
    requires com.zaxxer.hikari;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.lang3;

    opens org.nitramproductions.com.wordclassifier to javafx.fxml;
    exports org.nitramproductions.com.wordclassifier;
    exports org.nitramproductions.com.wordclassifier.controller;
    opens org.nitramproductions.com.wordclassifier.controller to javafx.fxml;
    exports org.nitramproductions.com.wordclassifier.model;
    exports org.nitramproductions.com.wordclassifier.controller.helper;
    opens org.nitramproductions.com.wordclassifier.controller.helper to javafx.fxml;
}