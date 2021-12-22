package org.nitramproductions.com.wordclassifier.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.nitramproductions.com.wordclassifier.model.Expression;
import org.nitramproductions.com.wordclassifier.model.Group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
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

    public static ObservableList<Expression> getAllExpressions() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = executeQuery("SELECT CONTENT, DATE_MODIFIED FROM EXPRESSION;");
        List<Expression> expressions = new ArrayList<>();
        while (resultSet.next()) {
            Expression expression = new Expression(resultSet.getString(1), resultSet.getTimestamp(2).toLocalDateTime());
            expressions.add(expression);
        }
        resultSet.close();
        closeConnection();
        return FXCollections.observableArrayList(expressions);
    }

    public static ObservableList<Group> getAllGroups() throws SQLException, ClassNotFoundException {
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

    public static ObservableList<Expression> getExpressionsFromGroup(Group group) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = executeQueryWithOneParameter("SELECT e.CONTENT, e.DATE_MODIFIED FROM EXPRESSION e, BELONGS_TO b WHERE e.CONTENT = b.CONTENT AND b.NAME = ?;", group.getName());
        List<Expression> expressions = new ArrayList<>();
        while (resultSet.next()) {
            Expression expression = new Expression(resultSet.getString(1), resultSet.getTimestamp(2).toLocalDateTime());
            expressions.add(expression);
        }
        resultSet.close();
        closeConnection();
        return FXCollections.observableArrayList(expressions);
    }

    public static ObservableList<Group> getGroupsFromExpression(Expression expression) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = executeQueryWithOneParameter("SELECT g.NAME, g.DATE_MODIFIED FROM \"GROUP\" g, BELONGS_TO b WHERE g.NAME = b.NAME AND b.CONTENT = ?;", expression.getContent());
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
        preparedStatement.closeOnCompletion();
        preparedStatement.execute();
        return preparedStatement.getResultSet();
    }

    public static ResultSet executeQueryWithOneParameter(String query, String parameter) throws SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, parameter);
        preparedStatement.closeOnCompletion();
        preparedStatement.execute();
        return preparedStatement.getResultSet();
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
