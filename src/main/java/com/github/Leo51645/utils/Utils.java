package com.github.Leo51645.utils;

import com.github.Leo51645.enums.Bank_Members_Columns;
import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.mysql.Database_BankMembers;
import com.github.Leo51645.services.Services;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;

import java.sql.Connection;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    private static final FallbackLogger FALLBACK_LOGGER = new FallbackLogger(FilePaths.LOG.filePaths, false, null);
    private static final FileLogger FILE_LOGGER = new FileLogger(LOGGER, FilePaths.LOG.filePaths, false, FALLBACK_LOGGER);

    public Utils() {
        FALLBACK_LOGGER.setFileLogger(FILE_LOGGER);
    }

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
        FILE_LOGGER.logIntoFile(Level.INFO, "Loading screen");
    }

    public static void printListOfActions_bl() {
        System.out.println("How can we help you with?");
        System.out.println("---------------------------------------------------------------------");
        stop(750);
        System.out.println("Creating a new account: .createAccount");
        System.out.println("Login into a account: .login");
        System.out.println("---------------------------------------------------------------------");
    }

    public static void printStartGUI(Connection connection, Services services) {

        Database_BankMembers database_bankMembers = new Database_BankMembers();
        Object email_object = services.getEmail();
        String email = email_object.toString();

        String salutation = null;
        Object gender_database = database_bankMembers.resultSet_selectSpecificColumn(connection, Bank_Members_Columns.GENDER.columnName, Bank_Members_Columns.EMAIL.columnName, email);
        String gender = gender_database.toString();
        if (gender.equals("male")) {
            salutation = "Mr. ";
        } else if (gender.equals("female")) {
            salutation = "Ms.";
        } else if (gender.isBlank()){
            salutation = "Mr. or Ms.";
        }

        Object lastName = database_bankMembers.resultSet_selectSpecificColumn(connection, Bank_Members_Columns.LASTNAME.columnName, Bank_Members_Columns.EMAIL.columnName, email);

        Object accountBalance_withoutEuro = database_bankMembers.resultSet_selectSpecificColumn(connection, Bank_Members_Columns.ACCOUNTBALANCE.columnName, Bank_Members_Columns.EMAIL.columnName, email);
        String accountBalance = String.format("%.2f€", accountBalance_withoutEuro);

        Object iban = database_bankMembers.resultSet_selectSpecificColumn(connection, Bank_Members_Columns.IBAN.columnName, Bank_Members_Columns.EMAIL.columnName, email);

        String time = getTime();


        System.out.println("---------------------------------------------------------------------\n" +
                           "|" + iban + "                                       " + time + " |\n" +
                           "|                        Welcome,                                    |\n" +
                           "|                      " + salutation + lastName + "                                   |\n" +
                           "|                                                                    |\n" +
                           "|                Account Balance:                                    |\n" +
                           "|                      " + accountBalance + "                                         |\n" +
                           "|                                                                    |\n" +
                           "|                How can we help you with?                           |\n" +
                           "|                  .transfer || .invest || .logout                   |\n" +
                           "---------------------------------------------------------------------");
        FILE_LOGGER.logIntoFile(Level.INFO, "Start GUI");
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
