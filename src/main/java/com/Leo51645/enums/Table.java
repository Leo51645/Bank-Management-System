package com.Leo51645.enums;

public enum Table {
    BANK_MEMBERS("Bank_Members"),
    TRANSACTION("Transactions");

    public final String tableName;

    Table(String table) {
        this.tableName = table;
    }
}
