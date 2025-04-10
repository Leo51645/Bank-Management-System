package com.github.Leo51645.services;

import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.enums.UserInput_beforeLogin;
import com.github.Leo51645.services.extras.ExtraFunctions;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;
import com.github.Leo51645.mysql.Database_BankMembers;
import com.github.Leo51645.services.account_management.createAccount.CreateAccount;
import com.github.Leo51645.services.account_management.login.Login;
import com.github.Leo51645.utils.Utils;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.*;

public class Services {

    private static final Logger LOGGER = Logger.getLogger(Services.class.getName());
    private final Scanner scanner = new Scanner(System.in);

    FallbackLogger fallbackLogger = new FallbackLogger(FilePaths.LOG.filepaths, false, null);
    FileLogger fileLogger = new FileLogger(LOGGER, FilePaths.LOG.filepaths, false, fallbackLogger);

    CreateAccount createAccount;
    Login login;
    ExtraFunctions extraFunctions;

    public Services(Database_BankMembers database_bankMembers) {
        this.login = new Login(database_bankMembers);
        this.createAccount = new CreateAccount(database_bankMembers);
        this.extraFunctions = new ExtraFunctions();
        fallbackLogger.setFileLogger(fileLogger);
    }

    // Method for getting the user input
    String getInput_beforeLogin() {
        Utils.printListOfActions_bl();
        System.out.print("Please enter your answer: ");
        String userAction = scanner.nextLine();
        System.out.println("---------------------------------------------------------------------");

            if (extraFunctions.isValidCommand(userAction) && (Objects.equals(userAction, UserInput_beforeLogin.LOGIN.command) || Objects.equals(userAction, UserInput_beforeLogin.CREATEACCOUNT.command))) {
                fileLogger.logIntoFile(Level.INFO, "Got user input before login successfully");
                return userAction;
            } else {
                fileLogger.logIntoFile(Level.INFO, "Invalid input by user: " + userAction);
                return null;
        }
    }

    // Method for create an account or login
    public Login getRegistration(Connection connection) {
        boolean registrationStatus = false;
        String userAction = null;
        while(!registrationStatus) {

            while(userAction == null) {
                userAction = getInput_beforeLogin();
                if(userAction == null) {
                    System.out.println("Invalid command. Please enter either '" + UserInput_beforeLogin.CREATEACCOUNT.command + "' or '" + UserInput_beforeLogin.LOGIN.command + "'");
                    System.out.println("---------------------------------------------------------------------");
                    Utils.stop(1250);
                }
            }

            if (userAction.equals(UserInput_beforeLogin.CREATEACCOUNT.command)) {
                registrationStatus = createAccount.createUserAccount(connection);
            } else if (userAction.equals(UserInput_beforeLogin.LOGIN.command)) {
                registrationStatus = login.loginIntoAccount(connection);
            } else {
                fileLogger.logIntoFile(Level.WARNING, "Failed to get the user correct user info and with that to create a new account or to login. Error code: 16");
                System.out.println("Something went wrong. Error code: 16");
            }
        }
        return login;
    }

}
