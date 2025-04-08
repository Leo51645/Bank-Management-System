package com.Leo51645.services;

import com.Leo51645.enums.*;
import com.Leo51645.mysql.*;
import com.Leo51645.services.account_management.createAccount.*;
import com.Leo51645.services.account_management.login.*;
import com.Leo51645.services.extras.*;
import com.Leo51645.services.fileLogging.FileLogger;
import com.Leo51645.utils.*;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.*;

public class Services {

    private final Database_BankMembers database_bankMembers;
    private static final Logger LOGGER = Logger.getLogger(Services.class.getName());
    private final Scanner scanner = new Scanner(System.in);

    FileLogger fileLogger = new FileLogger(LOGGER, "C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\log", false);

    CreateAccount createAccount;
    Login login;
    ExtraFunctions extraFunctions;

    public Services(Database_BankMembers database_bankMembers) {
        this.database_bankMembers = database_bankMembers;
        this.login = new Login(database_bankMembers);
        this.createAccount = new CreateAccount(database_bankMembers);
        this.extraFunctions = new ExtraFunctions();
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
