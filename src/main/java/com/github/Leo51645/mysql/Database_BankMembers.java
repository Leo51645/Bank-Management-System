package com.github.Leo51645.mysql;

import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Database_BankMembers extends Database implements IDatabase {

    private final Logger LOGGER = Logger.getLogger(Database_BankMembers.class.getName());

    private final FallbackLogger FALLBACK_LOGGER = new FallbackLogger(FilePaths.LOG.filePaths, false, null);
    private final FileLogger FILE_LOGGER = new FileLogger(LOGGER, FilePaths.LOG.filePaths, false, FALLBACK_LOGGER);

    public Database_BankMembers() {
        FALLBACK_LOGGER.setFileLogger(FILE_LOGGER);
    }

    // Methods for creating new queries
    @Override
    public String createInsertQuery() {
        return "Insert into Bank_Members(First_Name, Last_Name, Phone_Number, Email, account_pin, Password, gender) values (?, ?, ?, ?, ?, ?, ?)";
    }
    @Override
    public String createUpdateQuery(String columnToUpdate, String condition_column) {
        return "Update Bank_Members set " + columnToUpdate + " = ? where " + condition_column + " = ?";
    }
    @Override
    public String createDeleteQuery(String condition_column) {
        return "Delete from Bank_Members where " + condition_column + " = ?";
    }
    @Override
    public String createSelectQuery() {
        return "Select * from Bank_Members";
    }
    @Override
    public String createSelectQuery(String condition_column) {
        return "Select * from Bank_Members where " + condition_column + " = ?";
    }
    @Override
    public String createSelectQuery(String specific_column, String condition_column) {
        return "Select " + specific_column + " from Bank_Members where " + condition_column + " = ?";
    }

    // Method for inserting values in an insert query
    public void setValues_InsertQuery(PreparedStatement preparedStatement, String setFirst_Name, String setLast_Name, String setPhone_Number, String setEmail, String set_account_pin, String set_password, String setGender) {
        try {
            preparedStatement.setString(1, setFirst_Name);
            preparedStatement.setString(2, setLast_Name);
            preparedStatement.setString(3, setPhone_Number);
            preparedStatement.setString(4, setEmail);
            preparedStatement.setString(5, set_account_pin);
            preparedStatement.setString(6, set_password);
            preparedStatement.setString(7, setGender);
            FILE_LOGGER.logIntoFile(Level.INFO, "Set the values inside the insert query successfully");
        } catch (SQLException e) {
            FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to set variables correctly in insert query. Error code: 11", e);
            System.out.println("Failed to set variables correctly in insert query");
        }

    }

    // Method for getting one specific column from the database
    @Override
    public Object resultSet_selectSpecificColumn(Connection connection, String columnToSelect, String condition_column, Object valueOfConditionColumn) {
        Object data = null;
        String valueOfConditionColumn_new = valueOfConditionColumn.toString();
        String query = createSelectQuery(columnToSelect, condition_column);

        try (PreparedStatement preparedStatement = preparedStatement_create(connection, query)) {

            setValues_onePlaceholder(preparedStatement, valueOfConditionColumn_new);

            try (ResultSet resultSet = resultSet_create(preparedStatement)){
                String columnType;
                if (resultSet.next()) {
                    columnType = resultSet.getMetaData().getColumnTypeName(1);
                } else {
                    FILE_LOGGER.logIntoFile(Level.WARNING, "ResultSet is empty. Error code: 12");
                    System.out.println("Something went wrong, please try again. Error code: 12");
                    return null;
                }

                if (columnType == null) {
                    FILE_LOGGER.logIntoFile(Level.WARNING, "Empty column type in resultSet. Error Code: 13");
                    System.out.println("Something went wrong, please try again. Error code: 13");
                    return null;
                }

                data = switch (columnType) {
                    case "VARCHAR" -> resultSet.getString(1);
                    case "DECIMAL" -> resultSet.getDouble(1);
                    case "INT" -> resultSet.getInt(1);
                    default -> throw new IllegalStateException("Unexpected value: " + columnType);
                };
            }
            FILE_LOGGER.logIntoFile(Level.INFO, "Selected one column out of the database successfully");

        } catch (SQLException e) {
            FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to get one specific column out of the database. Error code: 14", e);
            System.out.println("Something went wrong, please try again. Error code: 14");
        }
        return data;
    }

    // Method for checking if there already exits something in the database
    @Override
    public boolean unique_exists(Connection connection, String columnToCheck, String inputToCheck) {
        boolean status = true;
        String query = createSelectQuery(columnToCheck);

        try (PreparedStatement preparedStatement = preparedStatement_create(connection, query)) {
            setValues_onePlaceholder(preparedStatement, inputToCheck);

            try (ResultSet resultSet = resultSet_create(preparedStatement)) {
                ArrayList<Object> data = resultSet_getAllValues(resultSet);
                status = !data.isEmpty();
            }
            FILE_LOGGER.logIntoFile(Level.INFO, "Checked if unique already exists successfully");
        } catch (SQLException e) {
            FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to check if a unique already exists. Error code: 15", e);
            System.out.println("Something went wrong, please try again. Error code: 15");
        }

        return status;
    }
}
