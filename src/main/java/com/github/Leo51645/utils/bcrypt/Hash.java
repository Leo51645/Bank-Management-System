package com.github.Leo51645.utils.bcrypt;

import com.github.Leo51645.enums.FilePaths;
import com.github.Leo51645.utils.fileLogging.FallbackLogger;
import com.github.Leo51645.utils.fileLogging.FileLogger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Hash {

    private static final Logger LOGGER = Logger.getLogger(Hash.class.getName());

    private final FallbackLogger FALLBACK_LOGGER = new FallbackLogger(FilePaths.LOG.filePaths, false, null);
    private final FileLogger FILE_LOGGER = new FileLogger(LOGGER, FilePaths.LOG.filePaths, false, FALLBACK_LOGGER);

    public Hash() {
        FALLBACK_LOGGER.setFileLogger(FILE_LOGGER);
    }

    // Method for hashing a password
    public String hashPassword(String originalPassword, int workFactor) {
        String hashedPassword = BCrypt.hashpw(originalPassword, BCrypt.gensalt(workFactor));
        FILE_LOGGER.logIntoFile(Level.FINE, "Hashed password successfully");
        return hashedPassword;
    }
    // Method for comparing two passwords with each other
    public boolean checkPassword(String inputPassword, String hashedPassword) {
        return BCrypt.checkpw(inputPassword, hashedPassword);
    }

}
