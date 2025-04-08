package com.Leo51645.services.account_management.login;

import com.Leo51645.enums.*;
import com.Leo51645.mysql.*;
import com.Leo51645.services.fileLogging.FileLogger;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login {

    private final Database_BankMembers database_bankMembers;
    private final Scanner scanner = new Scanner(System.in);

    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());

    FileLogger fileLogger = new FileLogger(LOGGER, "C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\log", false);


    private String email;

    public Login(Database_BankMembers database_bankMembers) {
        this.database_bankMembers = database_bankMembers;
    }

    // Method for login into an existing account
    public boolean loginIntoAccount(Connection connection) {
        System.out.println("Logging into your account:");
        System.out.println("---------------------------------------------------------------------");

        System.out.print("Please enter your email: ");
        email = scanner.nextLine();
        System.out.println("---------------------------------------------------------------------");
        System.out.print("Please enter your password: ");
        String password = scanner.nextLine();
        System.out.println("---------------------------------------------------------------------");

        boolean login_status = false;

        String selectQuery = database_bankMembers.createSelectQuery(Bank_Members_Columns.PASSWORD.columnName, Bank_Members_Columns.EMAIL.columnName);

        try (PreparedStatement preparedStatement = database_bankMembers.preparedStatement_create(connection, selectQuery)) {
            database_bankMembers.setValues_onePlaceholder(preparedStatement, email);

            try (ResultSet resultSet = database_bankMembers.resultSet_create(preparedStatement)) {
                if (resultSet.next()) {
                    String dbPassword = resultSet.getString("Password");
                    if (dbPassword != null && dbPassword.equals(password)) {
                        System.out.println("Login successful");
                        login_status = true;
                        fileLogger.logIntoFile(Level.INFO, "Logged into account in successfully");
                    } else {
                        System.out.println("Wrong password, please try again");
                        System.out.println("---------------------------------------------------------------------");
                        fileLogger.logIntoFile(Level.INFO, "Wrong password submitted by user");
                    }
                } else {
                    System.out.println("Email not found");
                    System.out.println("---------------------------------------------------------------------");
                    fileLogger.logIntoFile(Level.INFO, "Email given by user not found");
                }
            }
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to login. Error code: 23", e);
            System.out.println("Something went wrong, please try again. Error code: 23");
        }

        return login_status;
    }

    public String getEmail() {
        return this.email.toLowerCase();
    }
}
