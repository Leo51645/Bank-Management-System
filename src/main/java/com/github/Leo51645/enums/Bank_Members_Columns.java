package com.github.Leo51645.enums;

public enum Bank_Members_Columns {
    MYBANKID("MyBank_Id"),
    IBAN("IBAN"),
    ACCOUNTBALANCE("Account_balance"),
    FIRSTNAME("First_Name"),
    LASTNAME("Last_Name"),
    PHONENUMBER("Phone_Number"),
    EMAIL("Email"),
    ACCOUNTPIN("account_pin"),
    PASSWORD("Password"),
    ACCOUNTNUMBER("account_number");

    public final String columnName;

    Bank_Members_Columns(String columnName) {
        this.columnName = columnName;
    }
}
