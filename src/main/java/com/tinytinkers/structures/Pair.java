package com.tinytinkers.structures;

public class Pair<FIRST, SECOND> {

    private final FIRST first;
    private final SECOND second;

    private Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public static <FIRST, SECOND> Pair<FIRST, SECOND> of(
            FIRST first, SECOND second
    ) {
        return new Pair<>(first, second);
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

}
