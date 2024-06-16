package com.tinytinkers;

import java.util.List;

public class Condition {
    private final String operator;
    private final Object value;

    private Condition(String operator, Object value) {
        this.operator = operator;
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public static Condition equal(Object value) {
        return new Condition("=", value);
    }

    public static Condition like(String value) {
        var percentWrappedValue = "%" + value + "%";
        return new Condition(" LIKE ", percentWrappedValue);
    }

    public static Condition greaterThan(Number value) {
        return new Condition(">", value);
    }

    public static Condition lessThan(Number value) {
        return new Condition("<", value);
    }

    public static Condition between(Number start, Number end) {
        return new Condition(" BETWEEN ", List.of(start, end));
    }

    public static Condition in(List<?> values) {
        return new Condition(" IN ", values);
    }

}