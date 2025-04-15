package com.github.Leo51645.services.transaction.deposit;

import com.github.Leo51645.enums.BankMembersColumns;
import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.mysql.Database_BankMembers;
import com.github.Leo51645.services.Services;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Deposit {
    private final Database_BankMembers database_bankMembers;
    private final String email;

    private static final Logger LOGGER = Logger.getLogger(Deposit.class.getName());

    private final FallbackLogger FALLBACK_LOGGER = new FallbackLogger(FilePaths.LOG.filePaths, false, null);
    private final FileLogger FILE_LOGGER = new FileLogger(LOGGER, FilePaths.LOG.filePaths, false, FALLBACK_LOGGER);

    public Deposit(Database_BankMembers database_bankMembers, Services services) {
        this.database_bankMembers = database_bankMembers;
        this.email = services.getEmail();
        FALLBACK_LOGGER.setFileLogger(FILE_LOGGER);
    }

    // Method for depositing money onto an account
    public void depositMoney(Connection connection, double amountOfMoney) {
        double oldAccountBalance;

        // Getting the old account balance to add the money on
        Object accountBalance_Database = database_bankMembers.resultSet_selectSpecificColumn(connection, BankMembersColumns.ACCOUNTBALANCE.columnName, BankMembersColumns.EMAIL.columnName, email);
        String accountBalance_String = accountBalance_Database.toString();

        if (!(accountBalance_Database instanceof Double)) {
            FILE_LOGGER.logIntoFile(Level.INFO, "User tries to deposit money to a not existing account");
            System.out.println("Account is not existing, please create one before");
            return;
        }

        oldAccountBalance = Double.parseDouble(accountBalance_String);
        // Adding onto the old account balance the new amount
        double newAccountBalance = oldAccountBalance + amountOfMoney;

        // Updating the old account balance
        String query_update = database_bankMembers.createUpdateQuery(BankMembersColumns.ACCOUNTBALANCE.columnName, BankMembersColumns.IBAN.columnName);

        try (PreparedStatement preparedStatement = database_bankMembers.preparedStatement_create(connection, query_update)) {
            database_bankMembers.setValues_twoPlaceholder(preparedStatement, newAccountBalance, email);
            database_bankMembers.preparedStatement_executeUpdate(preparedStatement);
        } catch (SQLException e) {
            FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to deposit money. Error code: 27", e);
            System.out.println("Something went wrong, please try again. Error code: 27");
        }

    }
}
