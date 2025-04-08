package com.Leo51645;

import com.Leo51645.mysql.Database;
import com.Leo51645.mysql.Database_BankMembers;
import com.Leo51645.services.Services;
import com.Leo51645.services.account_management.login.Login;
import com.Leo51645.services.fileLogging.FileLogger;
import com.Leo51645.utils.Utils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

        Database_BankMembers database_bankMembers = new Database_BankMembers();
        Services services = new Services(database_bankMembers);

        File file = new File("C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\Database\\database_info.txt");

        final Logger LOGGER = Logger.getLogger(Database.class.getName());

        FileLogger fileLogger = new FileLogger(LOGGER, "C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\log", false);

        ArrayList<String> connection_info = database_bankMembers.connection_getInfos(file);
        Connection connection = database_bankMembers.connection_get(connection_info);

        boolean connectionStatus = database_bankMembers.connection_check(connection);
        System.out.println(connectionStatus);
        if (connectionStatus) {
            Utils.printLoadingScreen();
            Login login = services.getRegistration(connection);
            Utils.stop(1000);
            Utils.printStartGUI(connection, login);
        } else {
            fileLogger.logIntoFile(Level.SEVERE, "Database is currently offline: Error code: 00");
            System.out.println("Something went wrong, please try again. Error code: 00");
        }
    }
}
