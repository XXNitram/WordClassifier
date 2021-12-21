package org.nitramproductions.com.wordclassifier.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String databaseURL = "jdbc:h2:~/Documents/WordClassifier/database/WordClassifier;TIME ZONE=Europe/Berlin";
    private static final String driver = "org.h2.Driver";
    private static Connection connection = null;

    public static void initialize() throws SQLException, ClassNotFoundException {
        InputStream schema = ConnectionManager.class.getResourceAsStream("schema.sql");
        executeStatementsFromFile(schema);
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
    }

    public static void executeQuery(String query) throws SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
        closeConnection();
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
