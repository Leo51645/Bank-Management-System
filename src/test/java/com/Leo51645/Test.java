package com.Leo51645;

import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.mysql.Database_BankMembers;
import com.github.Leo51645.services.Services;
import com.github.Leo51645.services.account_management.createAccount.CreateAccount;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;

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
        File file = new File(FilePaths.DATABASEINFOS.filePaths);
        ArrayList<String> connection_infos = database_bankMembers.connection_getInfos(file);

        Connection connection = database_bankMembers.connection_get(connection_infos);

        final Logger LOGGER = Logger.getLogger(Test.class.getName());

        FallbackLogger fallbackLogger = new FallbackLogger(FilePaths.LOG.filePaths, false, null);
        FileLogger fileLogger = new FileLogger(LOGGER, FilePaths.LOG.filePaths, false, fallbackLogger);
        fallbackLogger.setFileLogger(fileLogger);



    }
}
