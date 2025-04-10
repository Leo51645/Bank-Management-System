package com.github.Leo51645.services.transaction.deposit;

import com.github.Leo51645.enums.Bank_Members_Columns;
import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.mysql.Database_BankMembers;
import com.github.Leo51645.services.account_management.login.Login;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Deposit {
    private final Database_BankMembers database_bankMembers;
    private final String email;

    private static final Logger LOGGER = Logger.getLogger(Deposit.class.getName());

    FallbackLogger fallbackLogger = new FallbackLogger(FilePaths.LOG.filepaths, false, null);
    FileLogger fileLogger = new FileLogger(LOGGER, FilePaths.LOG.filepaths, false, fallbackLogger);

    public Deposit(Database_BankMembers database_bankMembers, Login login) {
        this.database_bankMembers = database_bankMembers;
        this.email = login.getEmail();
        fallbackLogger.setFileLogger(fileLogger);
    }

    // Method for depositing money onto an account
    public void depositMoney(Connection connection, double amountOfMoney) {
        double oldAccountBalance;

        // Getting the old account balance to add the money on
        Object accountBalance_Database = database_bankMembers.resultSet_selectSpecificColumn(connection, Bank_Members_Columns.ACCOUNTBALANCE.columnName, Bank_Members_Columns.EMAIL.columnName, email);
        String accountBalance_String = accountBalance_Database.toString();

        if (!(accountBalance_Database instanceof Double)) {
            fileLogger.logIntoFile(Level.INFO, "User tries to deposit money to a not existing account");
            System.out.println("Account is not existing, please create one before");
            return;
        }

        oldAccountBalance = Double.parseDouble(accountBalance_String);
        // Adding onto the old account balance the new amount
        double newAccountBalance = oldAccountBalance + amountOfMoney;

        // Updating the old account balance
        String query_update = database_bankMembers.createUpdateQuery(Bank_Members_Columns.ACCOUNTBALANCE.columnName, Bank_Members_Columns.IBAN.columnName);

        try (PreparedStatement preparedStatement = database_bankMembers.preparedStatement_create(connection, query_update)) {
            database_bankMembers.setValues_twoPlaceholder(preparedStatement, newAccountBalance, email);
            database_bankMembers.preparedStatement_executeUpdate(preparedStatement);
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to deposit money. Error code: 27", e);
            System.out.println("Something went wrong, please try again. Error code: 27");
        }

    }
}
