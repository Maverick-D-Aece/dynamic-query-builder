package com.tinytinkers.constants;

import java.util.regex.Pattern;

public class Keywords {

    public static final String SELECT_ALL_FROM = "SELECT * FROM ";
    public static final String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM ";
    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    public static final String WHERE = " WHERE ";
    public static final String AND = " AND ";
    public static final String ORDER = " ORDER ";
    public static final String NULL = "NULL";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String GROUP_BY = " GROUP BY ";
    public static final String HAVING = " HAVING ";
    public static final String JOIN = " JOIN ";
    public static final String ON = " ON ";
    public static final String DISTINCT = "DISTINCT ";
    public static final String LIMIT = " LIMIT ";
    public static final String OFFSET = " OFFSET ";
    public static final Pattern UNSAFE_INPUT_PATTERN = Pattern.compile(";.*");

    private Keywords() { }

}
