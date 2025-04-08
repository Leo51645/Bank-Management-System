package com.Leo51645.mysql;

import com.Leo51645.services.fileLogging.FileLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.*;

public abstract class Database {

    private final Logger LOGGER = Logger.getLogger(Database.class.getName());

    FileLogger fileLogger = new FileLogger(LOGGER, "C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\log", false);

    // Method for getting the infos for the connection(url, user, password)
    public ArrayList<String> connection_getInfos(File file) {
        ArrayList<String> connection_infos = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                connection_infos.add(line);
            }
            fileLogger.logIntoFile(Level.INFO, "Got the database infos successfully");
        } catch (IOException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to get connection infos. Error code: 01", e);
            System.out.println("Something went wrong, please try again. Error code: 01");
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
                throw new IllegalArgumentException();
            }

            connection = DriverManager.getConnection(url, user, password);
            fileLogger.logIntoFile(Level.INFO, "Connected to database successfully");

        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.SEVERE, "Failed to connect to database. Error code: 02", e);
            System.out.println("Something went wrong, please try again. Error code: 02");
        } catch (IllegalArgumentException e) {
            fileLogger.logIntoFile(Level.SEVERE, "Failed to get correct database info. Error code: 03", e);
            System.out.println("Something went wrong, please try again. Error code: 03");
        }
        return connection;
    }
    // Method for checking if there is a connection to the database or not
    public boolean connection_check(Connection connection) {
        boolean connection_status = false;
        try {
            if (connection.isValid(2)) {
                connection_status = true;
                fileLogger.logIntoFile(Level.INFO, "Connection is valid");
            } else {
                fileLogger.logIntoFile(Level.SEVERE, "Connection is not valid");
            }
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.INFO, "Failed to check database connection. Error code: 04", e);
            System.out.println("Something went wrong, please try again - Error Code: 04");
        }
        return connection_status;
    }

    // Method for creating a statement which holds the query
    public PreparedStatement preparedStatement_create(Connection connection, String query) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            fileLogger.logIntoFile(Level.INFO, "Created preparedStatement successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.SEVERE, "Failed to create PreparedStatement. Error code: 05", e);
            System.out.println("Something went wrong, please try again. Error code: 05");
        }
        return preparedStatement;
    }
    // Method for executing the Query and with that also the SQL Code
    public void preparedStatement_executeUpdate(PreparedStatement preparedStatement) {
        try {
            preparedStatement.executeUpdate();
            fileLogger.logIntoFile(Level.INFO, "Executed the preparedStatement successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to execute query in the PreparedStatement. Error Code: 06", e);
            System.out.println("Something went wrong, please try again. Error code 06");
        }
    }

    // Methods for setting the values
    public <T, E> void setValues_twoPlaceholder(PreparedStatement preparedStatement, T x1, E x2) {
        try {
            preparedStatement.setObject(1, x1);
            preparedStatement.setObject(2, x2);
            fileLogger.logIntoFile(Level.INFO, "Set two values in the preparedStatement successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to set the two variables correctly in query. Error code: 07", e);
            System.out.println("Something went wrong, please try again. Error code: 07");
        }

    }
    public <T> void setValues_onePlaceholder(PreparedStatement preparedStatement, T x1) {
        try {
            preparedStatement.setObject(1, x1);
            fileLogger.logIntoFile(Level.INFO, "Set one value in the preparedStatement successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to set the one variable correctly in query. Error code: 08", e);
            System.out.println("Something went wrong, please try again. Error code: 08");
        }
    }

    // Method for creating a Result Set which holds for example the columns
    public ResultSet resultSet_create(PreparedStatement preparedStatement) {
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            fileLogger.logIntoFile(Level.INFO, "Created resultSet successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.SEVERE, "Failed to create resultSet. Error code: 09", e);
            System.out.println("Something went wrong, please try again. Error code: 09");
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
            fileLogger.logIntoFile(Level.INFO, "Got all values of the resultSet successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to get values of resultSet. Error code: 10", e);
            System.out.println("Something went wrong, please try again. Error code: 10");
        }
        return values_ResultSet;
    }

    }
