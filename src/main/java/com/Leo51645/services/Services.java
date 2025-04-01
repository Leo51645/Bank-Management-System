package com.Leo51645.services;

import com.Leo51645.enums.*;
import com.Leo51645.mysql.*;
import com.Leo51645.services.account_management.createAccount.*;
import com.Leo51645.services.account_management.login.*;
import com.Leo51645.services.extras.*;
import com.Leo51645.utils.*;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

public class Services {

    private final Database database;
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private final Scanner scanner = new Scanner(System.in);

    CreateAccount createAccount;
    Login login;
    ExtraFunctions extraFunctions;

    public Services(Database database) {
        this.database = database;
        this.createAccount = new CreateAccount(database);
        this.login = new Login(database);
        this.extraFunctions = new ExtraFunctions();
    }

    // Method for getting the user input
    public String getInput_beforeLogin() {
        Utils.printListOfActions_bl();
        System.out.print("Please enter your answer: ");
        String userAction = scanner.nextLine();
        System.out.println("---------------------------------------------------------------------");
        if (extraFunctions.isValidCommand(userAction)) {
            return userAction;
        } else return null;
    }

    // Method for create an account or login
    public void getRegistration(Connection connection) {
        boolean registrationStatus = false;
        String userAction = null;
        while(!registrationStatus) {

            while(userAction == null) {
                userAction = getInput_beforeLogin();
                if(userAction == null) {
                    System.out.println("Wrong input, please try again");
                    System.out.println("---------------------------------------------------------------------");
                    Utils.stop(1250);
                }
            }

            if (userAction.equals(UserInput_beforeLogin.CREATEACCOUNT.command)) {
                registrationStatus = createAccount.createUserAccount(connection);
            } else if (userAction.equals(UserInput_beforeLogin.LOGIN.command)) {
                registrationStatus = login.loginIntoAccount(connection);
            } else {
                System.out.println("Invalid command. Please enter either '" + UserInput_beforeLogin.CREATEACCOUNT.command + "' or '" + UserInput_beforeLogin.LOGIN.command + "'");
                userAction = null;
                System.out.println("---------------------------------------------------------------------");
                Utils.stop(1000);
            }
        }
    }

}
