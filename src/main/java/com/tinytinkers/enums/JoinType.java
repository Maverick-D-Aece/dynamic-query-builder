package com.tinytinkers.enums;

public enum JoinType {
    INNER("INNER"),
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    FULL("FULL"),
    CROSS("CROSS");

    private final String code;

    JoinType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
