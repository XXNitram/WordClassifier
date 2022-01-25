package org.nitramproductions.com.wordclassifier.database;

import org.h2.tools.Csv;
import org.nitramproductions.com.wordclassifier.database.helper.Columns;
import org.nitramproductions.com.wordclassifier.database.helper.QueryHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CSVManager {

    private final QueryHelper queryHelper = new QueryHelper();

    public CSVManager() {

    }

    public void writeSpecificGroupColumnsToCSV(List<Columns> columns) throws SQLException {
        String query = queryHelper.getSpecificGroupColumnsQuery(columns);
        try (Connection connection = DataSource.getConnection()) {
            new Csv().write(connection, "C:\\Users\\matti\\Documents\\Privat\\test.csv", query, null);
        }
    }

    public void writeSpecificExpressionColumnsToCSV(List<Columns> columns) throws SQLException {
        String query = queryHelper.getSpecificExpressionColumnsQuery(columns);
        try (Connection connection = DataSource.getConnection()) {
            new Csv().write(connection, "C:\\Users\\matti\\Documents\\Privat\\test.csv", query, null);
        }
    }

    public void writeSpecificBelongToColumnsToCSV(List<Columns> columns) throws SQLException {
        String query = queryHelper.getSpecificBelongsToColumnsQuery(columns);
        try (Connection connection = DataSource.getConnection()) {
            new Csv().write(connection, "C:\\Users\\matti\\Documents\\Privat\\test.csv", query, null);
        }
    }
}
