package com.github.Leo51645;

import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.mysql.Database_BankMembers;
import com.github.Leo51645.services.Services;
import com.github.Leo51645.services.account_management.login.Login;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;
import com.github.Leo51645.utils.Utils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

        Database_BankMembers database_bankMembers = new Database_BankMembers();
        Services services = new Services(database_bankMembers);

        File file = new File(FilePaths.DATABASEINFOS.filePaths);

        final Logger LOGGER = Logger.getLogger(Main.class.getName());

        FallbackLogger fallbackLogger = new FallbackLogger(FilePaths.LOG.filePaths, false, null);
        FileLogger fileLogger = new FileLogger(LOGGER, FilePaths.LOG.filePaths, false, fallbackLogger);
        fallbackLogger.setFileLogger(fileLogger);

        ArrayList<String> connection_info = database_bankMembers.connection_getInfos(file);
        Connection connection = database_bankMembers.connection_get(connection_info);

        boolean connectionStatus = database_bankMembers.connection_check(connection);
        if (connectionStatus) {
            Utils.printLoadingScreen();
            Utils.stop(1000);
            services.getRegistration(connection);
            Utils.printStartGUI(connection, services);
        } else {
            fileLogger.logIntoFile(Level.SEVERE, "Database is currently offline: Error code: 00");
            System.out.println("Something went wrong, please try again. Error code: 00");
        }
    }
}
