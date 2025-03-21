package com.tinytinkers;

import com.tinytinkers.enums.JoinType;
import com.tinytinkers.enums.Order;
import com.tinytinkers.structures.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tinytinkers.constants.Keywords.*;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ofPattern;

public class DynamicQueryBuilder {
    private String query = "";

    private DynamicQueryBuilder() {}

    public static DynamicQueryBuilder of() {
        return new DynamicQueryBuilder();
    }

    public DynamicQueryBuilder selectFrom(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("Table name cannot be null or blank");
        }
        tableName = sanitizeInput(tableName);
        query = SELECT_ALL_FROM + tableName;
        return this;
    }

    public DynamicQueryBuilder countFrom(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("Table name cannot be null or blank");
        }
        tableName = sanitizeInput(tableName);
        query = SELECT_COUNT_FROM + tableName;
        return this;
    }

    public DynamicQueryBuilder selectSpecificColumnsFrom(String tableName, String... columns) {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("Table name cannot be null or blank");
        }

        tableName = sanitizeInput(tableName);

        String[] sanitizedColumns = columns == null ? new String[0] :
                Arrays.stream(columns)
                        .filter(col -> col != null && !col.isBlank())
                        .map(this::sanitizeInput)
                        .toArray(String[]::new);

        if (sanitizedColumns.length == 0) {
            query = SELECT_ALL_FROM + tableName;
        } else {
            String specificColumnsQueryPart = String.join(",", sanitizedColumns);
            query = SELECT + specificColumnsQueryPart + FROM + tableName;
        }
        return this;
    }

    public DynamicQueryBuilder where(Map<String, Condition<?>> whereClauses) {
        if (whereClauses == null || whereClauses.isEmpty()) {
            return this;
        }

        Map<String, Condition<?>> filteredClauses = whereClauses.entrySet().stream()
                .filter(entry -> entry.getKey() != null && !entry.getKey().isBlank() && entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (filteredClauses.isEmpty()) {
            return this;
        }

        StringBuilder whereClauseBuilder = new StringBuilder(WHERE);
        boolean isFirstCondition = true;

        for (Map.Entry<String, Condition<?>> entry : filteredClauses.entrySet()) {
            String key = sanitizeInput(entry.getKey());
            Condition<?> condition = entry.getValue();
            Object value = condition.value();

            if (value == null) {
                continue;
            }

            if (!isFirstCondition) {
                whereClauseBuilder.append(AND);
            }

            String formattedValue = formatValue(value);
            whereClauseBuilder.append(key).append(condition.operator()).append(formattedValue);
            isFirstCondition = false;
        }

        if (!isFirstCondition) {
            query += whereClauseBuilder.toString();
        }

        return this;
    }

    public DynamicQueryBuilder orderBy(String column, Order order) {
        if (column == null || column.isBlank() || order == null) {
            return this;
        }
        column = sanitizeInput(column);
        query += ORDER_BY + column + " " + order.getCode();
        return this;
    }

    public DynamicQueryBuilder groupBy(String... columns) {
        if (columns == null || columns.length == 0) {
            return this;
        }

        String[] sanitizedColumns = Arrays.stream(columns)
                .filter(col -> col != null && !col.isBlank())
                .map(this::sanitizeInput)
                .toArray(String[]::new);

        if (sanitizedColumns.length == 0) {
            return this;
        }

        query += GROUP_BY + String.join(",", sanitizedColumns);
        return this;
    }

    public DynamicQueryBuilder having(String condition) {
        if (condition == null || condition.isBlank()) {
            return this;
        }
        condition = sanitizeInput(condition);
        query += HAVING + condition;
        return this;
    }

    public DynamicQueryBuilder join(JoinType joinType, String table, String onClause) {
        if (joinType == null || table == null || table.isBlank() || onClause == null || onClause.isBlank()) {
            return this;
        }
        table = sanitizeInput(table);
        onClause = sanitizeInput(onClause);
        query += " " + joinType.getCode() + JOIN + table + ON + onClause;
        return this;
    }

    public DynamicQueryBuilder subquery(String alias, String subquery) {
        if (alias == null || alias.isBlank() || subquery == null || subquery.isBlank()) {
            return this;
        }
        alias = sanitizeInput(alias);
        subquery = sanitizeInput(subquery);
        query += " (" + subquery + ") " + alias;
        return this;
    }

    public DynamicQueryBuilder distinct() {
        if (query.startsWith(SELECT)) {
            query = query.replaceFirst(SELECT, SELECT + DISTINCT);
        }
        return this;
    }

    public DynamicQueryBuilder alias(String alias) {
        if (alias == null || alias.isBlank()) {
            return this;
        }
        alias = sanitizeInput(alias);
        query += " AS " + alias;
        return this;
    }

    public DynamicQueryBuilder limit(int limit) {
        if (limit <= 0) {
            return this;
        }
        query += LIMIT + limit;
        return this;
    }

    public DynamicQueryBuilder offset(int offset) {
        if (offset < 0) {
            return this;
        }
        query += OFFSET + offset;
        return this;
    }

    public String buildToString() {
        if (query.isBlank()) {
            throw new IllegalStateException("Query could not be built after sanitization.");
        }
        return query.strip() + ";";
    }

    public String buildPrettyString() {
        if (query.isBlank()) {
            throw new IllegalStateException("Query could not be built after sanitization.");
        }
        String builtQuery = query.strip() + ";";
        return "\n--------------------------------------------------------" +
               builtQuery
                       .replace(SELECT, "\n SELECT ")
                       .replace(WHERE, "\n\t WHERE ")
                       .replace(AND, "\n\t\t AND ")
                       .replace(ORDER, "\n\t ORDER ") +
               "\n--------------------------------------------------------";
    }

    private String formatValue(Object value) {
        return switch (value) {
            case null -> NULL;
            case String str -> "'%s'".formatted(sanitizeInput(str));
            case LocalDateTime dateTime -> "'%s'".formatted(sanitizeInput(dateTime.format(ofPattern("yyyy-MM-dd HH:mm:ss"))));
            case LocalDate date -> "'%s'".formatted(sanitizeInput(date.format(ISO_LOCAL_DATE)));
            case Pair<?, ?> pair -> "%s AND %s".formatted(formatValue(pair.getFirst()), formatValue(pair.getSecond()));
            case List<?> list -> "(%s)".formatted(list.stream().map(this::formatValue).collect(Collectors.joining(",")));
            default -> String.valueOf(value);
        };
    }

    private String sanitizeInput(String input) {
        return Optional
                .ofNullable(input)
                .filter(i -> !i.isBlank())
                .map(i -> UNSAFE_INPUT_PATTERN.matcher(input).replaceAll("").trim()
                        .replace("'", "''")
                )
                .orElse("");
    }

}
