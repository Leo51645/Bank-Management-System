package com.github.Leo51645.enums;

public enum UserInput_beforeLogin {
    CREATEACCOUNT(".createAccount"),
    LOGIN(".login");

    public final String command;

    UserInput_beforeLogin(String command) {
        this.command = command;
    }
}
