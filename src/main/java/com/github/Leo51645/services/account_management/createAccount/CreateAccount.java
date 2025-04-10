package com.github.Leo51645.services.account_management.createAccount;

import com.github.Leo51645.enums.Bank_Members_Columns;
import com.github.Leo51645.enums.Countries;
import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.enums.Table;
import com.github.Leo51645.services.extras.ExtraFunctions;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;
import com.github.Leo51645.mysql.Database_BankMembers;
import com.github.Leo51645.utils.Utils;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class CreateAccount {

    private final Database_BankMembers database_bankMembers;
    private static final Logger LOGGER = Logger.getLogger(CreateAccount.class.getName());
    private final Scanner scanner = new Scanner(System.in);

    FallbackLogger fallbackLogger = new FallbackLogger(FilePaths.LOG.filepaths, false, null);
    FileLogger fileLogger = new FileLogger(LOGGER, FilePaths.LOG.filepaths, false, fallbackLogger);

    Iban iban;
    ExtraFunctions extraFunctions;

    public CreateAccount(Database_BankMembers database_bankMembers) {
        this.database_bankMembers = database_bankMembers;
        this.iban = new Iban(database_bankMembers);
        this.extraFunctions = new ExtraFunctions();
        fallbackLogger.setFileLogger(fileLogger);
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
                phoneNumber_exists = database_bankMembers.unique_exists(connection, Bank_Members_Columns.PHONENUMBER.columnName, input_PhoneNumber);

                if (phoneNumber_exists) {
                    System.out.println("Phone number already existing, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    fileLogger.logIntoFile(Level.INFO, "User wanted to create a new account with an already existing phone number");
                }
            }
            while (email_exists) {
                input_Email = createAccount_checkInputs(isValid_Email, Bank_Members_Columns.EMAIL.columnName);
                email_exists = database_bankMembers.unique_exists(connection, Bank_Members_Columns.EMAIL.columnName, input_Email);

                if (email_exists) {
                    System.out.println("Email already existing, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    fileLogger.logIntoFile(Level.INFO, "User wanted to create a new account with an already existing email");
                }
            }
            String input_Pin = createAccount_checkInputs(isValid_Pin, Bank_Members_Columns.ACCOUNTPIN.columnName);


            System.out.print("Please enter your password for your 'MyBank' account: ");
            String input_Password = scanner.nextLine();

            String phoneNumber = input_PhoneNumber.replaceAll("\\s+", "");

            // Inserting the main values like first name etc.
            createAccount_insertMainValues(connection, input_FirstName, input_LastName, phoneNumber, input_Email, input_Pin, input_Password);
            // Inserting the account number into the database
            createAccount_insertAccountNumber(connection, input_Email);

            // Inserting the iban value separately into the database
            String ibanNumber = iban.iban_create(connection, Countries.GREATBRITAIN.countryCode, input_Email);

            String iban_query = database_bankMembers.createUpdateQuery(Bank_Members_Columns.IBAN.columnName, Bank_Members_Columns.EMAIL.columnName);

            try (PreparedStatement preparedStatement_iban = database_bankMembers.preparedStatement_create(connection, iban_query)) {
                // Checking if the iban is valid
                if(extraFunctions.isValidIban(ibanNumber)) {
                    database_bankMembers.setValues_twoPlaceholder(preparedStatement_iban, ibanNumber, input_Email);
                    database_bankMembers.preparedStatement_executeUpdate(preparedStatement_iban);
                    System.out.println("Creating account successful");
                    login_status = true;
                    fileLogger.logIntoFile(Level.INFO, "Created account of user succefully");
                } else {
                    fileLogger.logIntoFile(Level.WARNING, "Failed to create Iban in createAccount. Error code: 22");
                    System.out.println("Something went wrong, please try again. Error code: 22");
                }
            } catch (SQLException e) {
                fileLogger.logIntoFile(Level.WARNING, "Failed to create Iban in createAccount. Error code: 22", e);
                System.out.println("Something went wrong, please try again. Error code: 22");
            }

        } catch (InputMismatchException e) {
            fileLogger.logIntoFile(Level.WARNING, "Wrong input submitted. Error code: 21", e);
            System.out.println("Something went wrong, please try again. Error code: 21");
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
                    fileLogger.logIntoFile(Level.INFO, "Input has to much letters at first and last name: " + inputOfUser);
                }
                Utils.stop(100);
            }
            else if (input_name.equals(Bank_Members_Columns.PHONENUMBER.columnName)) {
                isValidInput = extraFunctions.isValidPhoneNumber(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Invalid phone number, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    fileLogger.logIntoFile(Level.INFO, "Phone number is not a valid one: " + inputOfUser);
                }
                Utils.stop(100);
            }
            else if (input_name.equals(Bank_Members_Columns.EMAIL.columnName)) {
                isValidInput = extraFunctions.isValidEmail(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Invalid email, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    fileLogger.logIntoFile(Level.INFO, "Email is not a valid one: " + inputOfUser);
                }
                Utils.stop(100);
            }
            else if (input_name.equals(Bank_Members_Columns.ACCOUNTPIN.columnName)) {
                isValidInput = extraFunctions.isValidPin(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Invalid PIN, has to be 4 digits");
                    System.out.println("---------------------------------------------------------------------");
                    fileLogger.logIntoFile(Level.INFO, "Pin is not exactly 4 digits: " + inputOfUser);
                }
                Utils.stop(100);
            }

        }
        return inputOfUser;
    }
    // Method for inserting the main values into the database
    public void createAccount_insertMainValues(Connection connection, String input_FirstName, String input_LastName, String input_PhoneNumber, String input_Email, String input_Pin, String input_Password) {
        String query_insert = database_bankMembers.createInsertQuery();

        try (PreparedStatement preparedStatement_insertMainData = database_bankMembers.preparedStatement_create(connection, query_insert)) {
            database_bankMembers.setValues_InsertQuery(preparedStatement_insertMainData, input_FirstName, input_LastName, input_PhoneNumber, input_Email.toLowerCase(), input_Pin, input_Password);
            database_bankMembers.preparedStatement_executeUpdate(preparedStatement_insertMainData);
            fileLogger.logIntoFile(Level.INFO, "Inserted the main values successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to insert the main values into the database. Error code: 17", e);
            System.out.println("Something went wrong, please try again. Error code: 17");
        }
    }
    public void createAccount_insertAccountNumber(Connection connection, String input_Email) {
        String query_update =
                "UPDATE " + Table.BANK_MEMBERS.tableName +
                        " SET " + Bank_Members_Columns.ACCOUNTNUMBER.columnName + " = GenerateAccountNumber(MyBank_Id) " +
                        " WHERE " + Bank_Members_Columns.EMAIL.columnName + " = ?";

        try (PreparedStatement preparedStatement_updateAccountNumber = database_bankMembers.preparedStatement_create(connection, query_update)) {
            database_bankMembers.setValues_onePlaceholder(preparedStatement_updateAccountNumber, input_Email);
            database_bankMembers.preparedStatement_executeUpdate(preparedStatement_updateAccountNumber);
            fileLogger.logIntoFile(Level.INFO, "Inserted the account number successfully");
        } catch (SQLException e) {
            fileLogger.logIntoFile(Level.WARNING, "Failed to insert the account number into the database. Error code: 18", e);
            System.out.println("Something went wrong, please try again. Error code: 18");
        }
    }
}
