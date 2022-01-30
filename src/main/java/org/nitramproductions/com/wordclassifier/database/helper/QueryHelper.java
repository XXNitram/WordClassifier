package org.nitramproductions.com.wordclassifier.database.helper;

import java.util.List;

public class QueryHelper {

    public QueryHelper() {

    }

    public String getSpecificGroupColumnsQuery(List<Columns> columns) {
        String concatenatedColumns = getConcatenatedColumns(columns);
        return "SELECT ".concat(concatenatedColumns).concat(" FROM \"GROUP\";");
    }

    public String getSpecificExpressionColumnsQuery(List<Columns> columns) {
        String concatenatedColumns = getConcatenatedColumns(columns);
        return "SELECT ".concat(concatenatedColumns).concat(" FROM EXPRESSION;");
    }

    public String getSpecificBelongsToColumnsQuery(List<Columns> columns) {
        String concatenatedColumns = getConcatenatedColumns(columns);
        return "SELECT ".concat(concatenatedColumns).concat(" FROM BELONGS_TO;");
    }

    private String getConcatenatedColumns(List<Columns> columns) {
        String concatenatedColumns = "";
        for (Columns column : columns) {
            concatenatedColumns = concatenatedColumns.concat(column.name()).concat(",");
        }
        return concatenatedColumns;
    }
}
