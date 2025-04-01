package com.Leo51645;

import com.Leo51645.mysql.Database;
import com.Leo51645.services.Services;
import com.Leo51645.utils.Utils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Database database = new Database();
        Services services = new Services(database);

        File file = new File("C:\\Code\\IntelliJ IDEA\\Java\\Others\\Bank Management System(14.02.25)\\Bank Management System\\src\\main\\resources\\Database\\database_info.txt");
        ArrayList<String> connection_info = database.connection_getInfos(file);
        Connection connection = database.connection_get(connection_info);

        boolean connectionStatus = database.connection_check(connection);
        System.out.println(connectionStatus);
        if (connectionStatus) {
            Utils.printLoadingScreen();
            services.getRegistration(connection);
            Utils.stop(1000);
            Utils.printStartGUI(connection);
        } else {
            System.out.println("Database currently offline, please try later again");
        }




    }
}
