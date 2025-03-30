package com.Leo51645.utils;

import com.Leo51645.enums.Bank_Members_Columns;
import com.Leo51645.enums.Table;
import com.Leo51645.mysql.Database;
import com.Leo51645.services.account_management.login.Login;

import java.sql.Connection;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static void printLoadingScreen() {
        System.out.println("---------------------------------------------------------------------");
        System.out.println(
                ",--.   ,--.         ,-----.                  ,--.     \n" +
                "|   `.'   |,--. ,--.|  |) /_  ,--,--.,--,--, |  |,-.  \n" +
                "|  |'.'|  | \\  '  / |  .-.  \\' ,-.  ||      \\|     /  \n" +
                "|  |   |  |  \\   '  |  '--' /\\ '-'  ||  ||  ||  \\  \\  \n" +
                "`--'   `--'.-'  /   `------'  `--`--'`--''--'`--'`--' \n" +
                "           `---' ");
        System.out.println("---------------------------------------------------------------------");
        stop(3000);
    }

    public static void printListOfActions_bl() {
        System.out.println("How can we help you with?");
        System.out.println("---------------------------------------------------------------------");
        stop(750);
        System.out.println("Creating a new account: .createAccount");
        System.out.println("Login into a account: .login");
        System.out.println("---------------------------------------------------------------------");
    }

    public static void printStartGUI(Connection connection) {

        Database database = new Database();
        Login login = new Login(database);
        Object email_object = login.getEmail();
        String email = email_object.toString();

        Object lastName = database.resultSet_selectSpecificColumn(connection, Table.BANK_MEMBERS.tableName, Bank_Members_Columns.LASTNAME.columnName, Bank_Members_Columns.EMAIL.columnName, email);

        Object accountBalance_withoutEuro = database.resultSet_selectSpecificColumn(connection, Table.BANK_MEMBERS.tableName, Bank_Members_Columns.ACCOUNTBALANCE.columnName, Bank_Members_Columns.EMAIL.columnName, email);
        String accountBalance = String.format("%.2f€", accountBalance_withoutEuro);

        Object iban = database.resultSet_selectSpecificColumn(connection, Table.BANK_MEMBERS.tableName, Bank_Members_Columns.IBAN.columnName, Bank_Members_Columns.EMAIL.columnName, email);

        String time = getTime();


        System.out.println("---------------------------------------------------------------------\n" +
                           "|" + iban + "                                       " + time + " |\n" +
                           "|                        Welcome,                                    |\n" +
                           "|                   Mr. or Mrs " + lastName + "                               |\n" +
                           "|                                                                    |\n" +
                           "|                Account Balance:                                    |\n" +
                           "|                      " + accountBalance + "                                         |\n" +
                           "|                                                                    |\n" +
                           "|                How can we help you with?                           |\n" +
                           "|                  .transfer || .invest || .logout                   |\n" +
                           "---------------------------------------------------------------------");
    }

    public static void stop(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }
    public static String getTime() {
        LocalTime localTime = LocalTime.now();
        int time_hour = localTime.getHour();
        int time_minute = localTime.getMinute();

        String formattedHour = String.format("%02d", time_hour);
        String formattedMinute = String.format("%02d", time_minute);

        return formattedHour + ":" + formattedMinute;
    }

}
