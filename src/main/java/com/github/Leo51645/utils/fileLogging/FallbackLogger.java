package com.github.Leo51645.utils.fileLogging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FallbackLogger {

    private final Logger LOGGER = Logger.getLogger(FallbackLogger.class.getName());
    private FileHandler fileHandler;
    private final String logDirectory;

    private FileLogger fileLogger;

    public FallbackLogger(String logDirectory, boolean logIntoConsole, FileLogger fileLogger) {
        this.logDirectory = logDirectory;
        LOGGER.setUseParentHandlers(logIntoConsole);
        this.fileLogger = fileLogger;
    }

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
                fileLogger.logIntoFile(Level.SEVERE, "Failed to log: " + msg + " in fileLogger. Error code: 24", e);
                System.out.println("Something went wrong, please try again. Error code: 24");
            }
        } else {
            fileLogger.logIntoFile(Level.SEVERE, "File name is null at FallbackLogger. Error code. 25");
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
                fileLogger.logIntoFile(Level.SEVERE, "Failed to log: " + msg + " in fileLogger. Error code: 24", e);
                System.out.println("Something went wrong, please try again. Error code: 24");
            }
        } else {
            fileLogger.logIntoFile(Level.SEVERE, "File name is null at FallbackLogger. Error code. 25");
            System.out.println("Something is wrong, please try again. Error code: 25");
        }

    }

    String logFileCreate() {

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
        String now = localDateTime.format(timeFormatter);

        String file_name = Paths.get(logDirectory, now + ".log").toString();

        try {
            File file = new File(file_name);
            boolean status = file.createNewFile();

            if (status) {
                fileLogger.logIntoFile(Level.INFO, "Created log file successfully");
            }

        } catch (IOException e) {
            fileLogger.logIntoFile(Level.SEVERE, "Failed to create log file in FallbackLogger. Error code: 26", e);
            System.out.println("Something went wrong, please try again. Error code: 26");
        }
        return file_name;
    }

    public void setFileLogger(FileLogger fileLogger) {
        this.fileLogger = fileLogger;
    }

}
