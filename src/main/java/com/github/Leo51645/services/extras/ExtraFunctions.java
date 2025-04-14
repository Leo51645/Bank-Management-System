package com.github.Leo51645.services.extras;


public class ExtraFunctions {

    // Method for converting letters to numbers + 10
    public int letterToNumber(char letter) {
        char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X','Y', 'Z'};
        for(int i = 0; i < alphabet.length; i++) {
            if (letter == alphabet[i]) {
                return i + 10;
            }
        }
        return 0;
    }
    // Method to clean up a String(deleting letters)
    public String StringToClean(String StringToClean) {
        return StringToClean.replaceAll("[^\\d]", "");
    }

    // Method for checking if the inputs are valid
    public boolean isValidCommand(String command_toCheck) {
        return command_toCheck.matches("^\\.[a-zA-Z]+$");
    }
    public boolean isValidString(String string_toCheck) {
        return string_toCheck.matches("^[a-zA-Z]{1,50}$");
    }
    public boolean isValidPhoneNumber(String phone_number_toCheck) {
        return phone_number_toCheck.matches("^\\+?[1-9][0-9]{0,2}(\\s?\\(?\\d{1,4}\\)?\\s?)?(\\d{1,4}(\\s?[\\-]?\\s?\\d{1,4}){1,3}){1,3}$");
    }
    public boolean isValidEmail(String email_toCheck) {
        return email_toCheck.matches("^[A-Za-z0-9+_.-]+@(?:gmail\\.com|web\\.de|yahoo\\.com|gmx\\.de)$");
    }
    public boolean isValidPin(String number_toCheck) {
        return number_toCheck.matches("^[0-9]{4}$");
    }
    public boolean isValidIban(String iban_toCheck) {
        return iban_toCheck.matches("^[A-Z]{2}\\s?[0-9]{2}\\s?([A-Z0-9]{4}\\s?){2,7}[A-Z0-9]{0,4}$");
    }
    public boolean isValidGender(String gender_toCheck) {
        return gender_toCheck == null || gender_toCheck.matches("^(?i)(male|female|divers)?$");
    }
}
