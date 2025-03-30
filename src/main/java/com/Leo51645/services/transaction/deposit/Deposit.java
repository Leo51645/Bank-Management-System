package com.Leo51645.services.transaction.deposit;

import com.Leo51645.enums.*;
import com.Leo51645.mysql.Database;
import com.Leo51645.services.account_management.login.Login;

import java.sql.*;

public class Deposit {
    private final Database database;
    private final String email;

    public Deposit(Database database, Login login) {
        this.database = database;
        this.email = login.getEmail();
    }

    // Method for depositing money onto an account
    public void depositMoney(Connection connection, double amountOfMoney) {
        double accountBalance;

        // Getting the old account balance to add the money on
        Object accountBalance_Database = database.resultSet_selectSpecificColumn(connection, Table.BANK_MEMBERS.tableName, Bank_Members_Columns.ACCOUNTBALANCE.columnName, Bank_Members_Columns.EMAIL.columnName, email);
        String accountBalance_String = accountBalance_Database.toString();

        if (!(accountBalance_Database instanceof Double)) {
            System.out.println("Account is not existing, please create one before");
            return;
        }

        accountBalance = Double.parseDouble(accountBalance_String);
        double newAccountBalance = accountBalance + amountOfMoney;

        // Updating the old account balance
        String query_update = database.bankMembers_createUpdateQuery(Table.BANK_MEMBERS.tableName, Bank_Members_Columns.ACCOUNTBALANCE.columnName, Bank_Members_Columns.IBAN.columnName);

        try (PreparedStatement preparedStatement = database.preparedStatement_create(connection, query_update)) {
            database.bank_Members_setValues_UpdateQuery(preparedStatement, newAccountBalance, email);
            database.preparedStatement_executeUpdate(preparedStatement);
        } catch (SQLException e) {
            System.out.println("Failed to deposit money, please try again");
        }

    }
}
