package com.Leo51645;

import com.Leo51645.enums.Table;
import com.Leo51645.mysql.Database;
import com.Leo51645.services.Services;
import com.Leo51645.services.account_management.createAccount.CreateAccount;
import com.Leo51645.utils.Utils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;


public class Test {
    public static void main(String[] args) {

        Database database = new Database();
        Services services = new Services(database);
        CreateAccount createAccount = new CreateAccount(database);
        File file = new File("C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\Database\\database_info.txt");
        ArrayList<String> connection_infos = database.connection_getInfos(file);

        Connection connection = database.connection_get(connection_infos);

        Utils.printStartGUI(connection);

    }
}
