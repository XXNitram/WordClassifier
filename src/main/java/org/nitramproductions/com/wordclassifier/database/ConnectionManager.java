package org.nitramproductions.com.wordclassifier.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    private static final String databaseURL = "jdbc:h2:~/Documents/WordClassifier/database/WordClassifier;TIME ZONE=Europe/Berlin";
    private static final String driver = "org.h2.Driver";
    private static Connection connection = null;

    public static void initialize() throws SQLException, ClassNotFoundException {
        InputStream schema = ConnectionManager.class.getResourceAsStream("schema.sql");
        InputStream data = ConnectionManager.class.getResourceAsStream("data.sql");
        executeStatementsFromFile(schema);
        executeStatementsFromFile(data);
    }

    public static void executeStatementsFromFile(InputStream inputStream) throws SQLException, ClassNotFoundException {
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String currentLine;
            StringBuilder fullFile = new StringBuilder();
            try {
                while ((currentLine = bufferedReader.readLine()) != null) {
                    fullFile.append(currentLine);
                }
                executeQuery(fullFile.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeConnection();
    }

    public static ObservableList<Group> getGroups() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = executeQuery("SELECT NAME, DATE_MODIFIED FROM \"GROUP\";");
        List<Group> groups = new ArrayList<>();
        while (resultSet.next()) {
            Group group = new Group(resultSet.getString(1), resultSet.getTimestamp(2).toLocalDateTime());
            groups.add(group);
        }
        resultSet.close();
        closeConnection();
        return FXCollections.observableArrayList(groups);
    }

    public static ResultSet executeQuery(String query) throws SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        preparedStatement.closeOnCompletion();
        return resultSet;
    }

    private static void openConnection() throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        connection = DriverManager.getConnection(databaseURL);
    }

    private static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
