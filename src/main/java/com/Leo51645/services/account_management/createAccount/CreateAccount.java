package com.Leo51645.services.account_management.createAccount;

import com.Leo51645.enums.*;
import com.Leo51645.mysql.*;
import com.Leo51645.services.extras.ExtraFunctions;
import com.Leo51645.utils.*;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class CreateAccount {

    private final Database database;
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private final Scanner scanner = new Scanner(System.in);

    Iban iban;
    ExtraFunctions extraFunctions;

    public CreateAccount(Database database) {
        this.database = database;
        this.iban = new Iban(database);
        this.extraFunctions = new ExtraFunctions();
    }

    // Method for creating an account
    public boolean createUserAccount(Connection connection) {

        // Declaring variables
        boolean login_status = false;

        boolean isValid_FirstName = false;
        boolean isValid_LastName = false;
        boolean isValid_PhoneNumber = false;
        boolean isValid_Email = false;
        boolean isValid_Pin = false;

        String input_PhoneNumber = null;
        boolean phoneNumber_exists = true;
        String input_Email = null;
        boolean email_exists = true;

        // Getting the input and checking if the input is valid
        try {
            System.out.println("Creating a new Account:");
            System.out.println("---------------------------------------------------------------------");

            String input_FirstName = createAccount_checkInputs(isValid_FirstName, Bank_Members_Columns.FIRSTNAME.columnName);
            String input_LastName = createAccount_checkInputs(isValid_LastName, Bank_Members_Columns.LASTNAME.columnName);
            while (phoneNumber_exists) {
                input_PhoneNumber = createAccount_checkInputs(isValid_PhoneNumber, Bank_Members_Columns.PHONENUMBER.columnName);
                phoneNumber_exists = database.unique_exists(connection, Bank_Members_Columns.PHONENUMBER.columnName, input_PhoneNumber);

                if (phoneNumber_exists) {
                    System.out.println("Phone number already existing, please try again");
                    System.out.println("---------------------------------------------------------------------");
                }
            }
            while (email_exists) {
                input_Email = createAccount_checkInputs(isValid_Email, Bank_Members_Columns.EMAIL.columnName);
                email_exists = database.unique_exists(connection, Bank_Members_Columns.EMAIL.columnName, input_Email);

                if (email_exists) {
                    System.out.println("Email already existing, please try again");
                    System.out.println("---------------------------------------------------------------------");
                }
            }
            String input_Pin = createAccount_checkInputs(isValid_Pin, Bank_Members_Columns.ACCOUNTPIN.columnName);


            System.out.print("Please enter your password for your 'MyBank' account: ");
            String input_Password = scanner.nextLine();

            // Inserting the main values like first name etc.
            createAccount_insertMainValues(connection, input_FirstName, input_LastName, input_PhoneNumber, input_Email, input_Pin, input_Password);
            // Inserting the account number into the database
            createAccount_insertAccountNumber(connection, input_Email);

            // Inserting the iban value separately into the database
            String ibanNumber = iban.iban_create(connection, Countries.GREATBRITAIN.countryCode, input_Email);

            String iban_query = database.bankMembers_createUpdateQuery(Table.BANK_MEMBERS.tableName, Bank_Members_Columns.IBAN.columnName, Bank_Members_Columns.EMAIL.columnName);

            try (PreparedStatement preparedStatement_iban = database.preparedStatement_create(connection, iban_query)) {
                // Checking if the iban is valid
                if(extraFunctions.isValidIban(ibanNumber)) {
                    database.bank_Members_setValues_UpdateQuery(preparedStatement_iban, ibanNumber, input_Email);
                    database.preparedStatement_executeUpdate(preparedStatement_iban);
                    System.out.println("Creating account successful");
                    login_status = true;
                } else {
                    System.out.println("Something went wrong with creating the iban");
                }
            } catch (SQLException e) {
                System.out.println("Something went wrong with creating the iban");
            }

        } catch (InputMismatchException e) {
            LOGGER.log(Level.WARNING, "This input isn't allowed here, please try again\n" + e.getMessage());
            // back to the main menu
            // return for ending the method
            return false;
        }
        return login_status;
    }
    // Method for checking the input in the createAccount menu
    public String createAccount_checkInputs(boolean isValidInput, String input_name) {
        String inputOfUser = null;
        while(!isValidInput) {

            System.out.print("Please enter your " + input_name + ": ");
            inputOfUser = scanner.nextLine();
            System.out.println("---------------------------------------------------------------------");


            if (input_name.equals(Bank_Members_Columns.FIRSTNAME.columnName) || input_name.equals(Bank_Members_Columns.LASTNAME.columnName)) {
                isValidInput = extraFunctions.isValidString(inputOfUser);
                if (!isValidInput) {
                    System.out.println("To much letters, please try again");
                    System.out.println("---------------------------------------------------------------------");
                }
                Utils.stop(100);
            }
            else if (input_name.equals(Bank_Members_Columns.PHONENUMBER.columnName)) {
                isValidInput = extraFunctions.isValidPhoneNumber(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Wrong phone number, please try again");
                    System.out.println("---------------------------------------------------------------------");
                }
                Utils.stop(100);
            }
            else if (input_name.equals(Bank_Members_Columns.EMAIL.columnName)) {
                isValidInput = extraFunctions.isValidEmail(inputOfUser);
                if (!isValidInput) {
                    System.out.println("This email is not allowed here");
                    System.out.println("---------------------------------------------------------------------");
                }
                Utils.stop(100);
            }
            else if (input_name.equals(Bank_Members_Columns.ACCOUNTPIN.columnName)) {
                isValidInput = extraFunctions.isValidPin(inputOfUser);
                if (!isValidInput) {
                    System.out.println("The Pin has to be 4 digits");
                    System.out.println("---------------------------------------------------------------------");
                }
                Utils.stop(100);
            }

        }
        return inputOfUser;
    }
    // Method for inserting the main values into the database
    public void createAccount_insertMainValues(Connection connection, String input_FirstName, String input_LastName, String input_PhoneNumber, String input_Email, String input_Pin, String input_Password) {
        String query_insert = database.bankMembers_createInsertQuery(Table.BANK_MEMBERS.tableName);

        try (PreparedStatement preparedStatement_insertMainData = database.preparedStatement_create(connection, query_insert)) {
            database.bankMembers_setValues_InsertQuery(preparedStatement_insertMainData, input_FirstName, input_LastName, input_PhoneNumber, input_Email.toLowerCase(), input_Pin, input_Password);
            database.preparedStatement_executeUpdate(preparedStatement_insertMainData);
        } catch (SQLException e) {
            System.out.println("Failed to insert the main values into the database");
        }
    }
    public void createAccount_insertAccountNumber(Connection connection, String input_Email) {
        String query_update =
                "UPDATE " + Table.BANK_MEMBERS.tableName +
                        " SET " + Bank_Members_Columns.ACCOUNTNUMBER.columnName + " = GenerateAccountNumber(MyBank_Id) " +
                        " WHERE " + Bank_Members_Columns.EMAIL.columnName + " = ?";

        try (PreparedStatement preparedStatement_updateAccountNumber = database.preparedStatement_create(connection, query_update)) {
            database.bank_Members_setValues_onePlaceholder(preparedStatement_updateAccountNumber, input_Email);
            database.preparedStatement_executeUpdate(preparedStatement_updateAccountNumber);
        } catch (SQLException e) {
            System.out.println("Failed to insert the account number into the database");
        }
    }
}
