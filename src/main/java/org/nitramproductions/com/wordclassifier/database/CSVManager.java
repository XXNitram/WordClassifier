package org.nitramproductions.com.wordclassifier.database;

import org.h2.tools.Csv;
import org.nitramproductions.com.wordclassifier.database.helper.Columns;
import org.nitramproductions.com.wordclassifier.database.helper.QueryHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CSVManager {

    private final QueryHelper queryHelper = new QueryHelper();

    public CSVManager() {

    }

    public void writeSpecificGroupColumnsToCSV(List<Columns> columns, String filePath) throws SQLException {
        String query = queryHelper.getSpecificGroupColumnsQuery(columns);
        writeToCSV(filePath, query);
    }

    public void writeSpecificExpressionColumnsToCSV(List<Columns> columns, String filePath) throws SQLException {
        String query = queryHelper.getSpecificExpressionColumnsQuery(columns);
        writeToCSV(filePath, query);
    }

    public void writeSpecificBelongToColumnsToCSV(List<Columns> columns, String filePath) throws SQLException {
        String query = queryHelper.getSpecificBelongsToColumnsQuery(columns);
        writeToCSV(filePath, query);
    }

    private void writeToCSV(String filePath, String query) throws SQLException {
        try (Connection connection = DataSource.getConnection()) {
            new Csv().write(connection, filePath, query, "ISO8859_15");
        }
    }

    public void readAndInsertGroupsFromCSV(String filePath) throws SQLException {
        ResultSet resultSet = readFromCSV(filePath);
        if (!resultSet.next()) {
            throw new IllegalStateException();
        }
        String query = "INSERT INTO \"GROUP\" VALUES (?, COALESCE(?, CURRENT_TIMESTAMP()), COALESCE(?, CURRENT_TIMESTAMP()))";
        try (Connection connection = DataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                do {
                    preparedStatement.setObject(1, resultSet.getString("NAME"));
                    preparedStatement.setObject(2, resultSet.getString("DATE_MODIFIED"));
                    preparedStatement.setObject(3, resultSet.getString("CREATION_DATE"));
                    preparedStatement.addBatch();
                } while (resultSet.next());
                preparedStatement.executeBatch();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void readAndInsertExpressionFromCSV(String filePath) throws SQLException {
        ResultSet resultSet = readFromCSV(filePath);
        if (!resultSet.next()) {
            throw new IllegalStateException();
        }
        String query = "INSERT INTO EXPRESSION VALUES (?, COALESCE(?, CURRENT_TIMESTAMP()), COALESCE(?, CURRENT_TIMESTAMP()))";
        try (Connection connection = DataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                do {
                    preparedStatement.setObject(1, resultSet.getString("CONTENT"));
                    preparedStatement.setObject(2, resultSet.getString("DATE_MODIFIED"));
                    preparedStatement.setObject(3, resultSet.getString("CREATION_DATE"));
                    preparedStatement.addBatch();
                } while (resultSet.next());
                preparedStatement.executeBatch();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void readAndInsertBelongsToFromCSV(String filePath) throws SQLException {
        ResultSet resultSet = readFromCSV(filePath);
        if (!resultSet.next()) {
            throw new IllegalStateException();
        }
        String query = "INSERT INTO BELONGS_TO VALUES (?, ?)";
        try (Connection connection = DataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                do {
                    preparedStatement.setObject(1, resultSet.getString("NAME"));
                    preparedStatement.setObject(2, resultSet.getString("CONTENT"));
                    preparedStatement.addBatch();
                } while (resultSet.next());
                preparedStatement.executeBatch();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private ResultSet readFromCSV(String filePath) throws SQLException {
        return new Csv().read(filePath, null, null);
    }
}
