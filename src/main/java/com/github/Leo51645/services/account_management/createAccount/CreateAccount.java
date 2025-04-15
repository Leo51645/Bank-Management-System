package com.github.Leo51645.services.account_management.createAccount;

import com.github.Leo51645.enums.BankMembersColumns;
import com.github.Leo51645.enums.Countries;
import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.enums.Table;
import com.github.Leo51645.services.extras.ExtraFunctions;
import com.github.Leo51645.utils.bcrypt.Hash;
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

    private final FallbackLogger FALLBACK_LOGGER = new FallbackLogger(FilePaths.LOG.filePaths, false, null);
    private final FileLogger FILE_LOGGER = new FileLogger(LOGGER, FilePaths.LOG.filePaths, false, FALLBACK_LOGGER);

    private final Iban iban;
    private final ExtraFunctions extraFunctions;
    private final Hash hash;

    private String input_Email;

    public CreateAccount(Database_BankMembers database_bankMembers) {
        this.database_bankMembers = database_bankMembers;
        this.iban = new Iban(database_bankMembers);
        this.extraFunctions = new ExtraFunctions();
        this.hash = new Hash();
        FALLBACK_LOGGER.setFileLogger(FILE_LOGGER);
    }

    // Method for creating an account
    public boolean createUserAccount(Connection connection) {

        // Declaring variables
        boolean login_status = false;

        String input_PhoneNumber = null;
        boolean phoneNumber_exists = true;
        input_Email = null;
        boolean email_exists = true;

        // Getting the input and checking if the input is valid
        try {
            System.out.println("Creating a new Account:");
            System.out.println("---------------------------------------------------------------------");

            String input_FirstName = createAccount_checkInputs(BankMembersColumns.FIRSTNAME.columnName);
            String input_LastName = createAccount_checkInputs(BankMembersColumns.LASTNAME.columnName);
            String input_Gender = createAccount_checkInputs(BankMembersColumns.GENDER.columnName);
            while (phoneNumber_exists) {
                input_PhoneNumber = createAccount_checkInputs(BankMembersColumns.PHONENUMBER.columnName);
                phoneNumber_exists = database_bankMembers.unique_exists(connection, BankMembersColumns.PHONENUMBER.columnName, input_PhoneNumber);

                if (phoneNumber_exists) {
                    System.out.println("Phone number already existing, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    FILE_LOGGER.logIntoFile(Level.FINE, "User wanted to create a new account with an already existing phone number");
                }
            }
            while (email_exists) {
                input_Email = createAccount_checkInputs(BankMembersColumns.EMAIL.columnName);
                email_exists = database_bankMembers.unique_exists(connection, BankMembersColumns.EMAIL.columnName, input_Email);

                if (email_exists) {
                    System.out.println("Email already existing, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    FILE_LOGGER.logIntoFile(Level.FINE, "User wanted to create a new account with an already existing email");
                }
            }
            String input_Pin = createAccount_checkInputs(BankMembersColumns.ACCOUNTPIN.columnName);

            System.out.print("Please enter your password for your 'MyBank' account: ");
            String input_Password = scanner.nextLine();

            String phoneNumber = input_PhoneNumber.replaceAll("\\s+", "");

            // Inserting the main values like first name etc.
            createAccount_insertMainValues(connection, input_FirstName, input_LastName, phoneNumber, input_Email, input_Pin, input_Password, input_Gender);
            // Inserting the account number into the database
            createAccount_insertAccountNumber(connection, input_Email);

            // Inserting the iban value separately into the database
            String ibanNumber = iban.iban_create(connection, Countries.GREATBRITAIN.countryCode, input_Email);

            String iban_query = database_bankMembers.createUpdateQuery(BankMembersColumns.IBAN.columnName, BankMembersColumns.EMAIL.columnName);

            try (PreparedStatement preparedStatement_iban = database_bankMembers.preparedStatement_create(connection, iban_query)) {
                // Checking if the iban is valid
                if(ibanNumber != null && extraFunctions.isValidIban(ibanNumber)) {
                    database_bankMembers.setValues_twoPlaceholder(preparedStatement_iban, ibanNumber, input_Email);
                    database_bankMembers.preparedStatement_executeUpdate(preparedStatement_iban);
                    System.out.println("---------------------------------------------------------------------");
                    System.out.println("Created account successfully");
                    login_status = true;
                    FILE_LOGGER.logIntoFile(Level.INFO, "Created account of user successfully");
                } else {
                    FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to create Iban in createAccount. Error code: 22");
                    System.out.println("Something went wrong, please try again. Error code: 22");
                }
            } catch (SQLException e) {
                FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to create Iban in createAccount. Error code: 22", e);
                System.out.println("Something went wrong, please try again. Error code: 22");
            }

        } catch (InputMismatchException e) {
            FILE_LOGGER.logIntoFile(Level.WARNING, "Wrong input submitted. Error code: 21", e);
            System.out.println("Something went wrong, please try again. Error code: 21");
            return false;
        }
        return login_status;
    }
    // Method for checking the input in the createAccount menu
    public String createAccount_checkInputs(String input_name) {
        String inputOfUser = null;
        boolean isValidInput = false;

        while(!isValidInput) {

            if (input_name.equals(BankMembersColumns.GENDER.columnName)) {
                System.out.print("Please enter your gender (optional): ");
            } else {
                System.out.print("Please enter your " + input_name + ": ");
            }
            inputOfUser = scanner.nextLine();
            System.out.println("---------------------------------------------------------------------");

            if (input_name.equals(BankMembersColumns.FIRSTNAME.columnName) || input_name.equals(BankMembersColumns.LASTNAME.columnName)) {
                isValidInput = extraFunctions.isValidString(inputOfUser);
                if (!isValidInput) {
                    System.out.println("To much letters, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    FILE_LOGGER.logIntoFile(Level.FINE, "Input has to much letters at first and last name: " + inputOfUser);
                }
                Utils.stop(100);
            }
            else if (input_name.equals(BankMembersColumns.PHONENUMBER.columnName)) {
                isValidInput = extraFunctions.isValidPhoneNumber(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Invalid phone number, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    FILE_LOGGER.logIntoFile(Level.FINE, "Phone number is not a valid one: " + inputOfUser);
                }
                Utils.stop(100);
            }
            else if (input_name.equals(BankMembersColumns.EMAIL.columnName)) {
                isValidInput = extraFunctions.isValidEmail(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Invalid email, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    FILE_LOGGER.logIntoFile(Level.FINE, "Email is not a valid one: " + inputOfUser);
                }
                Utils.stop(100);
            }
            else if (input_name.equals(BankMembersColumns.ACCOUNTPIN.columnName)) {
                isValidInput = extraFunctions.isValidPin(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Invalid PIN, has to be 4 digits");
                    System.out.println("---------------------------------------------------------------------");
                    FILE_LOGGER.logIntoFile(Level.FINE, "Pin is not exactly 4 digits: " + inputOfUser);
                }
                Utils.stop(100);
            }
            else if (input_name.equals(BankMembersColumns.GENDER.columnName)) {
                isValidInput = extraFunctions.isValidGender(inputOfUser);
                if (!isValidInput) {
                    System.out.println("Invalid gender; please enter one of these: 'Male', 'Female', 'Divers'\n or just leave it blank if you don't want to answer");
                    System.out.println("---------------------------------------------------------------------");
                    FILE_LOGGER.logIntoFile(Level.FINE, "Gender is not valid: " + inputOfUser);
                }
                Utils.stop(100);
            }

        }
        return inputOfUser;
    }
    // Method for inserting the main values into the database
    public void createAccount_insertMainValues(Connection connection, String input_FirstName, String input_LastName, String input_PhoneNumber, String input_Email, String input_Pin, String input_Password, String input_Gender) {
        String query_insert = database_bankMembers.createInsertQuery();

        String hashedPassword = hash.hashPassword(input_Password, 12);

        try (PreparedStatement preparedStatement_insertMainData = database_bankMembers.preparedStatement_create(connection, query_insert)) {
            database_bankMembers.setValues_InsertQuery(preparedStatement_insertMainData, input_FirstName, input_LastName, input_PhoneNumber, input_Email.toLowerCase(), input_Pin, hashedPassword, input_Gender.toLowerCase());
            database_bankMembers.preparedStatement_executeUpdate(preparedStatement_insertMainData);
            FILE_LOGGER.logIntoFile(Level.INFO, "Inserted the main values successfully");
        } catch (SQLException e) {
            FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to insert the main values into the database. Error code: 17", e);
            System.out.println("Something went wrong, please try again. Error code: 17");
        }
    }
    public void createAccount_insertAccountNumber(Connection connection, String input_Email) {
        String query_update =
                "UPDATE " + Table.BANK_MEMBERS.tableName +
                        " SET " + BankMembersColumns.ACCOUNTNUMBER.columnName + " = GenerateAccountNumber(MyBank_Id) " +
                        " WHERE " + BankMembersColumns.EMAIL.columnName + " = ?";

        try (PreparedStatement preparedStatement_updateAccountNumber = database_bankMembers.preparedStatement_create(connection, query_update)) {
            database_bankMembers.setValues_onePlaceholder(preparedStatement_updateAccountNumber, input_Email);
            database_bankMembers.preparedStatement_executeUpdate(preparedStatement_updateAccountNumber);
            FILE_LOGGER.logIntoFile(Level.INFO, "Inserted the account number successfully");
        } catch (SQLException e) {
            FILE_LOGGER.logIntoFile(Level.WARNING, "Failed to insert the account number into the database. Error code: 18", e);
            System.out.println("Something went wrong, please try again. Error code: 18");
        }
    }

    public String get_Email() {
        return input_Email;
    }
}
