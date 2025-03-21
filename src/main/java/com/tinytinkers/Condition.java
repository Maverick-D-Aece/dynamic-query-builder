package com.tinytinkers;

import com.tinytinkers.structures.Pair;

import java.util.List;
import java.util.Optional;

import static com.tinytinkers.constants.Keywords.UNSAFE_INPUT_PATTERN;
import static com.tinytinkers.constants.Operators.*;

public record Condition<T>(
        String operator,
        T value
) {
    public static <T extends Comparable<T>> Condition<T> equal(T value) {
        return new Condition<>(EQUALS, value);
    }

    public static Condition<String> anyLike(String value) {
        var sanitizedInput = sanitizeInput(value);

        return Optional.of(sanitizedInput)
                .filter(s -> !s.isBlank())
                .map(s -> new Condition<>(LIKE, PERCENT_SIGN + s + PERCENT_SIGN))
                .orElse(new Condition<>(LIKE, null));
    }

    public static Condition<String> beginLike(String value) {
        var sanitizedInput = sanitizeInput(value);

        return Optional.of(sanitizedInput)
                .filter(s -> !s.isBlank())
                .map(s -> new Condition<>(LIKE, s + PERCENT_SIGN))
                .orElse(new Condition<>(LIKE, null));
    }

    public static Condition<String> endLike(String value) {
        var sanitizedInput = sanitizeInput(value);

        return Optional.of(sanitizedInput)
                .filter(s -> !s.isBlank())
                .map(s -> new Condition<>(LIKE, PERCENT_SIGN + s))
                .orElse(new Condition<>(LIKE, null));
    }

    public static <T extends Comparable<T>> Condition<T> greaterThan(T value) {
        return new Condition<>(GREATER_THAN, value);
    }

    public static <T extends Comparable<T>> Condition<T> lessThan(T value) {
        return new Condition<>(LESS_THAN, value);
    }

    public static <T extends Comparable<T>> Condition<Pair<T, T>> between(T start, T end) {
        return new Condition<>(BETWEEN, Pair.of(start, end));
    }

    public static <T extends Comparable<T>> Condition<List<T>> in(List<T> values) {
        return new Condition<>(IN, values);
    }

    private static String sanitizeInput(String input) {
        return Optional
                .ofNullable(input)
                .filter(i -> !i.isBlank())
                .map(i -> UNSAFE_INPUT_PATTERN.matcher(input).replaceAll("").trim())
                .orElse("");
    }

}