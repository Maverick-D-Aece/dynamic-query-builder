package com.tinytinkers.enums;

public enum Order {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private final String code;

    Order(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
