package com.coursework.project.logging;

import org.springframework.boot.logging.java.SimpleFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CustomLogger {
    private static final Logger logger = Logger.getLogger(CustomLogger.class.getName());
    private static FileHandler fileHandler;

    static {
        try {
            fileHandler = new FileHandler("log.txt", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);

            logger.setLevel(Level.ALL);
            fileHandler.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    public static void logError(String message) {
        logger.log(Level.SEVERE, message);
    }
}