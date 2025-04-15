package com.github.Leo51645.enums;

public enum UserInputBeforeLogin {
    CREATEACCOUNT(".createAccount"),
    LOGIN(".login");

    public final String command;

    UserInputBeforeLogin(String command) {
        this.command = command;
    }
}
