package com.Leo51645.services.account_management.login;

import com.Leo51645.enums.*;
import com.Leo51645.mysql.*;

import java.sql.*;
import java.util.Scanner;

public class Login {

    private final Database database;
    private final Scanner scanner = new Scanner(System.in);

    private String email;

    public Login(Database database) {
        this.database = database;
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

        String selectQuery = database.bank_Members_createSelectQuery(Table.BANK_MEMBERS.tableName, Bank_Members_Columns.PASSWORD.columnName, Bank_Members_Columns.EMAIL.columnName);

        try (PreparedStatement preparedStatement = database.preparedStatement_create(connection, selectQuery)) {
            database.bank_Members_setValues_onePlaceholder(preparedStatement, email);

            try (ResultSet resultSet = database.resultSet_create(preparedStatement)) {
                if (resultSet.next()) {
                    String dbPassword = resultSet.getString("Password");
                    if (dbPassword != null && dbPassword.equals(password)) {
                        System.out.println("Login successful");
                        login_status = true;
                    } else {
                        System.out.println("Wrong password, please try again");
                        System.out.println("---------------------------------------------------------------------");
                    }
                } else {
                    System.out.println("Email not found");
                    System.out.println("---------------------------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to login, please try again");
        }

        return login_status;
    }

    public String getEmail() {
        return this.email.toLowerCase();
    }
}
