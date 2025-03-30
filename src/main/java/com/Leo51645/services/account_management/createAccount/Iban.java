package com.Leo51645.services.account_management.createAccount;

import com.Leo51645.mysql.*;
import com.Leo51645.services.extras.*;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;

public class Iban {

    private final Database database;

    ExtraFunctions extraFunctions;

    public Iban(Database database) {
        this.database = database;
        this.extraFunctions = new ExtraFunctions();
    }

    // Method for creating the IBAN
    public String iban_create(Connection connection, String countryCode, String emailOfUser) { // CountryCode GB for Great Britain
        final String bank_number = "10001000"; // 8 digits

        char firstChar_of_countryCode = countryCode.charAt(0);
        char secondCharOfCountryCode = countryCode.charAt(1);

        int firstNumber_of_countryCode = extraFunctions.letterToNumber(firstChar_of_countryCode);
        int secondNumber_of_country = extraFunctions.letterToNumber(secondCharOfCountryCode);

        String accountNumber = iban_accountNumber_get(database, connection, emailOfUser);
        String checkNumber = iban_checkNumber_create(bank_number, accountNumber, firstNumber_of_countryCode, secondNumber_of_country);

        if (accountNumber != null && checkNumber != null) {
            return countryCode + " " + checkNumber + " " + bank_number + " " + accountNumber;
        } else return null;
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

        String query = database.bank_Members_createSelectQuery("bank_members", "account_number", "Email");

        try (PreparedStatement preparedStatement = database.preparedStatement_create(connection, query)) {
            database.bank_Members_setValues_onePlaceholder(preparedStatement, emailOfUser);

            try (ResultSet resultSet = database.resultSet_create(preparedStatement);) {
                resultSetData = database.resultSet_getAllValues(resultSet);

                if(resultSetData.isEmpty()) {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get account number of the database");
        }

        return resultSetData.getFirst().toString();
    }

}
