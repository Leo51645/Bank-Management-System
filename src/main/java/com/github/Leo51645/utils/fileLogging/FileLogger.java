package com.github.Leo51645.utils.fileLogging;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class FileLogger {

    private FileHandler fileHandler;
    private final Logger LOGGER;
    private final String logDirectory;

    private final FallbackLogger fallbackLogger;

    public FileLogger(Logger LOGGER, String logDirectory, boolean logIntoConsole, FallbackLogger fallbackLogger) {
        this.LOGGER = LOGGER;
        this.logDirectory = logDirectory;
        this.fallbackLogger = fallbackLogger;
        LOGGER.setUseParentHandlers(logIntoConsole);
    }

    // Methods for logging the messages and errors into files
    public synchronized void logIntoFile(Level level, String msg) {
        String file_name = logFileCreate();

        if (file_name != null) {
            try {
                fileHandler = new FileHandler(file_name, true);
                fileHandler.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(fileHandler);
                LOGGER.log(level, msg);
                LOGGER.removeHandler(fileHandler);
                fileHandler.close();
            } catch (IOException e) {
                fallbackLogger.logIntoFile(Level.SEVERE, "Failed to log: " + msg + " in fileLogger. Error code: 24", e);
                System.out.println("Something went wrong, please try again. Error code: 24");
            }
        } else {
            fallbackLogger.logIntoFile(Level.SEVERE, "File name is null at FileLogger. Error code. 25");
            System.out.println("Something is wrong, please try again. Error code: 25");
        }
    }
    public synchronized void logIntoFile(Level level, String msg, Exception exception) {
        String file_name = logFileCreate();

        if (file_name != null) {
            try {
                fileHandler = new FileHandler(file_name, true);
                fileHandler.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(fileHandler);
                LOGGER.log(level, msg, exception);
                LOGGER.removeHandler(fileHandler);
                fileHandler.close();
            } catch (IOException e) {
                fallbackLogger.logIntoFile(Level.SEVERE, "Failed to log: " + msg + " in fileLogger. Error code: 24", e);
                System.out.println("Something went wrong, please try again. Error code: 24");
            }
        } else {
            fallbackLogger.logIntoFile(Level.SEVERE, "File name is null at FileLogger. Error code. 25");
            System.out.println("Something is wrong, please try again. Error code: 25");
        }

    }

    // Method for creating the file to log in
    private String logFileCreate() {

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
        String now = localDateTime.format(timeFormatter);

        String file_name = Paths.get(logDirectory, now + ".log").toString();

            try {
                File file = new File(file_name);
                boolean status = file.createNewFile();

                if (status) {
                    fallbackLogger.logIntoFile(Level.INFO, "Created file successfully");
                }
            } catch (IOException e) {
                fallbackLogger.logIntoFile(Level.SEVERE, "Failed to create log file in fileLogger. Error code: 26", e);
                System.out.println("Something went wrong, please try again. Error code: 26");
            }
        return file_name;
    }

}
