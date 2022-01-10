package org.nitramproductions.com.wordclassifier.database;

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

    public ConnectionManager() {}

    public void initialize() throws SQLException, IOException {
        InputStream schema = getClass().getResourceAsStream("schema.sql");
        InputStream data = getClass().getResourceAsStream("data.sql");
        executeStatementsFromFile(schema);
        // executeStatementsFromFile(data);
    }

    public void executeStatementsFromFile(InputStream inputStream) throws SQLException, IOException {
        if (inputStream != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String currentLine;
                StringBuilder fullFile = new StringBuilder();
                while ((currentLine = bufferedReader.readLine()) != null) {
                    fullFile.append(currentLine);
                }
                try (Connection connection = DataSource.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(fullFile.toString())) {
                    preparedStatement.execute();
                }
            }
        }
    }

    public List<Expression> getAllExpressions() throws SQLException {
        String query = "SELECT CONTENT, DATE_MODIFIED FROM EXPRESSION;";
        List<Expression> expressions;
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            expressions = new ArrayList<>();
            while (resultSet.next()) {
                Expression expression = new Expression();
                expression.setContent(resultSet.getString("CONTENT"));
                expression.setDateModified(resultSet.getTimestamp("DATE_MODIFIED").toLocalDateTime());
                expressions.add(expression);
            }
        }
        return expressions;
    }

    public List<Group> getAllGroups() throws SQLException {
        String query = "SELECT NAME, DATE_MODIFIED FROM \"GROUP\";";
        List<Group> groups;
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            groups = new ArrayList<>();
            while (resultSet.next()) {
                Group group = new Group();
                group.setName(resultSet.getString("NAME"));
                group.setDateModified(resultSet.getTimestamp("DATE_MODIFIED").toLocalDateTime());
                groups.add(group);
            }
        }
        return groups;
    }

    public List<Expression> getExpressionsBelongingToGroup(Group group) throws SQLException {
        String query = "SELECT e.CONTENT, e.DATE_MODIFIED FROM EXPRESSION e, BELONGS_TO b WHERE e.CONTENT = b.CONTENT AND b.NAME = ?;";
        List<Expression> expressions;
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, group.getName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                expressions = new ArrayList<>();
                while (resultSet.next()) {
                    Expression expression = new Expression();
                    expression.setContent(resultSet.getString("CONTENT"));
                    expression.setDateModified(resultSet.getTimestamp("DATE_MODIFIED").toLocalDateTime());
                    expressions.add(expression);
                }
            }
        }
        return expressions;
    }

    public List<Group> getGroupsFromExpression(Expression expression) throws SQLException {
        String query = "SELECT g.NAME, g.DATE_MODIFIED FROM \"GROUP\" g, BELONGS_TO b WHERE g.NAME = b.NAME AND b.CONTENT = ?;";
        List<Group> groups;
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, expression.getContent());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                groups = new ArrayList<>();
                while (resultSet.next()) {
                    Group group = new Group();
                    group.setName(resultSet.getString("NAME"));
                    group.setDateModified(resultSet.getTimestamp("DATE_MODIFIED").toLocalDateTime());
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    public void addNewGroup(Group newGroup) throws SQLException {
        String query = "INSERT INTO \"GROUP\" (NAME) VALUES (?);";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, newGroup.getName());
            preparedStatement.executeUpdate();
        }
    }

    public void addNewExpression(Expression newExpression) throws SQLException {
        String query = "INSERT INTO EXPRESSION (CONTENT) VALUES (?);";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, newExpression.getContent());
            preparedStatement.executeUpdate();
        }
    }

    public void addNewBelongToRelation(Group group, Expression expression) throws SQLException {
        String query = "INSERT INTO BELONGS_TO (NAME, CONTENT) VALUES (?, ?);";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, group.getName());
            preparedStatement.setObject(2, expression.getContent());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteGroup(Group group) throws SQLException {
        String query = "DELETE FROM \"GROUP\" WHERE NAME = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, group.getName());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteExpression(Expression expression) throws SQLException {
        String query = "DELETE FROM EXPRESSION WHERE CONTENT = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, expression.getContent());
            preparedStatement.executeUpdate();
        }
    }
}
