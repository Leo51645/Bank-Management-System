package com.Leo51645.mysql;

import com.Leo51645.enums.*;
import com.Leo51645.services.Services;
import com.Leo51645.services.account_management.login.Login;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.*;
import java.io.*;

public class Database {

    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    // Method for getting the infos for the connection(url, user, password)
    public ArrayList<String> connection_getInfos(File file) {
        ArrayList<String> connection_infos = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                connection_infos.add(line);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to get necessary info about the database\n" + e.getMessage(), e);
        }

        return connection_infos;
    }
    // Method for creating the connection to the Database
    public Connection connection_get(ArrayList<String> connection_infos)  {
        Connection connection = null;

        String url;
        String user;
        String password;

        try {
            if (connection_infos.size() == 3) {
                url = connection_infos.get(0);
                user = connection_infos.get(1);
                password = connection_infos.get(2);
            } else {
                throw new IllegalArgumentException("Incorrect database info");
            }

            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database\n" + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database info correctly\n" + e.getMessage(), e);
        }
        return connection;
    }
    // Method for checking if there is a connection to the database or not
    public boolean connection_check(Connection connection) {
        boolean connection_status = false;
        try {
            if (connection.isValid(2)) {
                connection_status = true;
                LOGGER.info("Connection is valid");
            } else {
                LOGGER.warning("Connection is not valid");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to check connection\n" + e.getMessage(), e);
        }
        return connection_status;
    }

    // Method for creating different query's for the table bank_members
    public String bankMembers_createInsertQuery(String table) {
        return "Insert into " + table + " (First_Name, Last_Name, Phone_Number, Email, account_pin, Password) values (?, ?, ?, ?, ?, ?)";
    }
    public String bankMembers_createUpdateQuery(String table, String columnToUpdate, String condition_column) {
        return "Update " + table + " set " + columnToUpdate + " = ? where " + condition_column + " = ?";
    }
    public String bankMembers_createDeleteQuery(String table, String condition_column) {
        return "Delete from "+ table + " where " + condition_column + " = ?";
    }

    public String bankMembers_createSelectQuery(String table) {
        return "Select * from " + table;
    }
    public String bank_Members_createSelectQuery(String table, String condition_column) {
        return "Select * from " + table + " where " + condition_column + " = ?";
    }
    public String bank_Members_createSelectQuery(String table, String specific_column, String condition_column) {
        return "Select " + specific_column + " from " + table + " where " + condition_column + " = ?";
    }

    // Method for creating a statement which holds the query
    public PreparedStatement preparedStatement_create(Connection connection, String query) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to create PreparedStatement\n" + e.getMessage(), e);
        }
        return preparedStatement;
    }
    // Method for executing the Query and with that also the SQL Code
    public void preparedStatement_executeUpdate(PreparedStatement preparedStatement) {
        try {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to execute Query\n" + e.getMessage(), e);
        }
    }

    // Methods for setting the values
    public void bankMembers_setValues_InsertQuery(PreparedStatement preparedStatement, String setFirst_Name, String setLast_Name, String setPhone_Number, String setEmail, String set_account_pin, String set_password) {
        try {
            preparedStatement.setString(1, setFirst_Name);
            preparedStatement.setString(2, setLast_Name);
            preparedStatement.setString(3, setPhone_Number);
            preparedStatement.setString(4, setEmail);
            preparedStatement.setString(5, set_account_pin);
            preparedStatement.setString(6, set_password);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to set variables correctly in insert query\n" + e.getMessage(), e);
        }

    }
    public <T, E> void bank_Members_setValues_UpdateQuery(PreparedStatement preparedStatement, T x1, E x2) {
        try {
            preparedStatement.setObject(1, x1);
            preparedStatement.setObject(2, x2);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to set variables correctly in update query\n" + e.getMessage(), e);
        }

    }
    public void bank_Members_setValues_onePlaceholder(PreparedStatement preparedStatement, String x1) {
        try {
            preparedStatement.setString(1, x1);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to set variables correctly in delete query\n" + e.getMessage(), e);
        }
    }

    // Method for creating a Result Set which holds for example the columns
    public ResultSet resultSet_create(PreparedStatement preparedStatement) {
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to create ResultSet\n" + e.getMessage(), e);
        }
        return resultSet;
    }

    // Method for getting the datatypes and all values of the resultSet
    public ArrayList<Object> resultSet_getAllValues(ResultSet resultSet) {
        ArrayList<Object> values_ResultSet = new ArrayList<>();

        try {
            while (resultSet.next()) {
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    values_ResultSet.add(resultSet.getObject(i));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to get values of resultSet\n" + e.getMessage(), e);
        }
        return values_ResultSet;
    }
    // Method for printing out all values of the resultSet
    public void resultSet_printAllValues(ArrayList<Object> valuesOfResultSet) {
        for (Object value : valuesOfResultSet) {
            System.out.println(value);
        }
    }
    // Method for getting one specific column from the database
    public Object resultSet_selectSpecificColumn(Connection connection, String table, String columnToSelect, String condition_column, Object valueOfConditionColumn) {
        Object data = null;
        String valueOfConditionColumn_new = valueOfConditionColumn.toString();
        String query = bank_Members_createSelectQuery(table, columnToSelect, condition_column);

        try (PreparedStatement preparedStatement = preparedStatement_create(connection, query)) {

            bank_Members_setValues_onePlaceholder(preparedStatement, valueOfConditionColumn_new);

            try (ResultSet resultSet = resultSet_create(preparedStatement)){
                String columnType;
                if (resultSet.next()) {
                    columnType = resultSet.getMetaData().getColumnTypeName(1);
                } else {
                    LOGGER.log(Level.WARNING, "Result set is empty\n");
                    return null;
                }

                if (columnType == null) {
                    LOGGER.log(Level.WARNING, "Empty column type\n");
                    return null;
                }

                switch (columnType) {
                    case "VARCHAR": data = resultSet.getString(1);
                        break;
                    case "DECIMAL": data = resultSet.getDouble(1);
                        break;
                    case "INT": data = resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "\n" + e.getMessage(), e);
        }

        return data;
    }

    // Method for checking if there already exits something in the database
    public boolean unique_exists(Connection connection, String columnToCheck, String inputToCheck) {
        boolean status = true;
        String query = bank_Members_createSelectQuery(Table.BANK_MEMBERS.tableName, columnToCheck);

        try (PreparedStatement preparedStatement = preparedStatement_create(connection, query)) {
            bank_Members_setValues_onePlaceholder(preparedStatement, inputToCheck);

            try (ResultSet resultSet = resultSet_create(preparedStatement)) {
                ArrayList<Object> data = resultSet_getAllValues(resultSet);
                status = !data.isEmpty();
            }
        } catch (SQLException e) {
            System.out.println("Failed to check if theres already a unique one");
        }

        return status;
    }

    }
