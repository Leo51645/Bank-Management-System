package com.Leo51645.enums;

public enum Countries {
    GERMANY("DE"),
    GREATBRITAIN("GB");

    public final String countryCode;

    Countries(String countryCode) {
        this.countryCode = countryCode;
    }
}
