package com.github.Leo51645.enums;

public enum BankMembersColumns {
    MYBANKID("MyBank_Id"),
    IBAN("IBAN"),
    ACCOUNTBALANCE("Account_balance"),
    FIRSTNAME("First_Name"),
    LASTNAME("Last_Name"),
    PHONENUMBER("Phone_Number"),
    EMAIL("Email"),
    ACCOUNTPIN("account_pin"),
    PASSWORD("Password"),
    ACCOUNTNUMBER("account_number"),
    GENDER("gender");

    public final String columnName;

    BankMembersColumns(String columnName) {
        this.columnName = columnName;
    }
}
