package com.Leo51645.services.account_management.createAccount;

import com.Leo51645.enums.Bank_Members_Columns;
import com.Leo51645.mysql.*;
import com.Leo51645.services.extras.*;
import com.Leo51645.services.fileLogging.FileLogger;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Iban {

    private final Database_BankMembers database_bankMembers;

    private static final Logger LOGGER = Logger.getLogger(Iban.class.getName());

    FileLogger fileLogger = new FileLogger(LOGGER, "C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\log", false);


    ExtraFunctions extraFunctions;

    public Iban(Database_BankMembers database_bankMembers) {
        this.database_bankMembers = database_bankMembers;
        this.extraFunctions = new ExtraFunctions();
    }

    // Method for creating the IBAN
    public String iban_create(Connection connection, String countryCode, String emailOfUser) { // CountryCode GB for Great Britain
        final String bank_number = "10001000"; // 8 digits

        char firstChar_of_countryCode = countryCode.charAt(0);
        char secondCharOfCountryCode = countryCode.charAt(1);

        int firstNumber_of_countryCode = extraFunctions.letterToNumber(firstChar_of_countryCode);
        int secondNumber_of_country = extraFunctions.letterToNumber(secondCharOfCountryCode);

        String accountNumber = iban_accountNumber_get(database_bankMembers, connection, emailOfUser);
        String checkNumber = iban_checkNumber_create(bank_number, accountNumber, firstNumber_of_countryCode, secondNumber_of_country);

        if (accountNumber != null && checkNumber != null) {
            return countryCode + " " + checkNumber + " " + bank_number + " " + accountNumber;
        } else {
            fileLogger.logIntoFile(Level.WARNING, "Failed to create iban. Error code: 20");
            System.out.println("Something went wrong, please try again. Error code: 20");
            return null;
        }
    }

    // Method for creating the check number of the iban
    public String iban_checkNumber_create(String bank_number, String accountNumber, Integer firstNumber_of_countryCode, Integer secondNumber_of_country) {
        String firstNumber = String.valueOf(firstNumber_of_countryCode);
        String secondNumber = String.valueOf(secondNumber_of_country);
        accountNumber = extraFunctions.StringToClean(accountNumber);

        String iban_withoutCheckDigits = bank_number + accountNumber + firstNumber + secondNumber + "00";

        BigInteger iban = new BigInteger(iban_withoutCheckDigits);

        int mod = iban.mod(BigInteger.valueOf(97)).intValue();
        int checkNumber = 98 - mod;

        return String.format("%02d", checkNumber);
    }
    // Method for getting the account number for the iban
    public String iban_accountNumber_get(Database database, Connection connection, String emailOfUser) {
        ArrayList<Object> resultSetData = null;

        String query = database_bankMembers.createSelectQuery(Bank_Members_Columns.ACCOUNTNUMBER.columnName, Bank_Members_Columns.EMAIL.columnName);

        try (PreparedStatement preparedStatement = database.preparedStatement_create(connection, query)) {
            database_bankMembers.setValues_onePlaceholder(preparedStatement, emailOfUser);

            try (ResultSet resultSet = database.resultSet_create(preparedStatement)) {
                resultSetData = database.resultSet_getAllValues(resultSet);

                if(resultSetData.isEmpty()) {
                    return null;
                }
            }
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to get account number out of the database. Error code: 19", e);
            System.out.println("Something went wrong, please try again. Error code: 19");
        }

        return resultSetData.getFirst().toString();
    }

}
