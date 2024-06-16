package com.tinytinkers;

import jdk.jshell.spi.ExecutionControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DynamicQueryBuilderUtil {

    private String query;

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

    private DynamicQueryBuilderUtil() {
    }

    public static DynamicQueryBuilderUtil dynamicQueryBuilder() {
        return new DynamicQueryBuilderUtil();
    }

    public DynamicQueryBuilderUtil selectFrom(String tableName) {
        query = "SELECT * FROM " + tableName;
        return this;
    }

    public DynamicQueryBuilderUtil selectSpecificColumnsFrom(String tableName, String ... columns) {
        var specificColumnsQueryPart = String.join(",", columns);

        query = "SELECT " + specificColumnsQueryPart + " FROM " + tableName;
        return this;
    }

    public DynamicQueryBuilderUtil where(Map<String, Condition> whereClauses) {
        query += " WHERE ";

        whereClauses
                .forEach((key, clause) ->
                        query += key + clause.getOperator() + getValueByObjectType(clause.getValue()) + " AND "
                );

        query = query.substring(0, query.length() - 5);
        return this;
    }

    public DynamicQueryBuilderUtil orderBy(String column, Order order) {
        query += " ORDER BY " + column + " " + order.getCode();
        return this;
    }

    public DynamicQueryBuilderUtil groupBy() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public DynamicQueryBuilderUtil having() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public DynamicQueryBuilderUtil join() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public DynamicQueryBuilderUtil subquery() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public DynamicQueryBuilderUtil distinct() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public DynamicQueryBuilderUtil alias() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public DynamicQueryBuilderUtil limit() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public DynamicQueryBuilderUtil offset() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Awaiting implementation...");
    }

    public String buildToString() {
        return query = query.strip() + ";";
    }

    public String buildPrettyString() {
        query = query.strip() + ";";

        var prettyQuery = "\n--------------------------------------------------------";
        prettyQuery += query
                .replaceAll("SELECT ", "\n SELECT ")
                .replaceAll(" WHERE ", "\n\t WHERE ")
                .replaceAll(" AND ", "\n\t\t AND ")
                .replaceAll(" ORDER ", "\n\t ORDER ");
        prettyQuery += "\n--------------------------------------------------------";

        return prettyQuery;
    }

    private Object getValueByObjectType(Object value) {
        return switch (value) {
            case null -> "NULL";
            case String ignored -> quoteAndGet(value);
            case LocalDateTime ignored -> quoteAndGet(value);
            case LocalDate ignored -> quoteAndGet(value);

            case List<?> values -> {
                var concatenates = new AtomicReference<>("(");
                values.forEach(
                        v -> concatenates.updateAndGet(
                                val -> val + getValueByObjectType(v) + ",")
                );
                var concatenated = concatenates.get();
                yield concatenated.substring(0, concatenated.length() - 1) + ")";
            }

            default -> value;
        };
    }

    private Object quoteAndGet(Object value) {
        return "'" + value + "'";
    }

}
