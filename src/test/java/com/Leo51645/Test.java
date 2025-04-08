package com.Leo51645;

import com.Leo51645.mysql.Database_BankMembers;
import com.Leo51645.services.Services;
import com.Leo51645.services.account_management.createAccount.CreateAccount;
import com.Leo51645.services.fileLogging.FileLogger;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    public static void main(String[] args) {

        Database_BankMembers database_bankMembers = new Database_BankMembers();
        Services services = new Services(database_bankMembers);
        CreateAccount createAccount = new CreateAccount(database_bankMembers);
        File file = new File("C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\Database\\database_info.txt");
        ArrayList<String> connection_infos = database_bankMembers.connection_getInfos(file);

        Connection connection = database_bankMembers.connection_get(connection_infos);

        final Logger LOGGER = Logger.getLogger(Test.class.getName());

        FileLogger fileLogger = new FileLogger(LOGGER, "C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\log", false);

        fileLogger.logIntoFile(Level.WARNING, "Hello");


    }
}
