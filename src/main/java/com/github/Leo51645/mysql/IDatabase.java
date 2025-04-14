package com.github.Leo51645.mysql;

import java.sql.*;

public interface IDatabase {

    // Method for creating different query's for the table bank_members
    String createInsertQuery();
    String createUpdateQuery(String columnToUpdate, String condition_column);
    String createDeleteQuery(String condition_column);
    String createSelectQuery();
    String createSelectQuery(String condition_column);
    String createSelectQuery(String specific_column, String condition_column);

    // Method for getting one specific column from the database
    Object resultSet_selectSpecificColumn(Connection connection, String columnToSelect, String condition_column, Object valueOfConditionColumn);

    // Method for checking if there already exits something in the database
    boolean unique_exists(Connection connection, String columnToCheck, String inputToCheck);
}
