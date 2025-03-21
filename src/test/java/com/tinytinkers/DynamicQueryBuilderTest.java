package com.tinytinkers;

import com.tinytinkers.enums.JoinType;
import com.tinytinkers.enums.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DynamicQueryBuilderTest {

    @Test
    void testSelectFrom() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .buildToString();
        assertEquals("SELECT * FROM users;", query);
    }

    @Test
    void testCountFrom() {
        String query = DynamicQueryBuilder.of()
                .countFrom("users")
                .buildToString();
        assertEquals("SELECT COUNT(*) FROM users;", query);
    }

    @Test
    void testSelectSpecificColumnsFrom() {
        String query = DynamicQueryBuilder.of()
                .selectSpecificColumnsFrom("users", "id", "name")
                .buildToString();
        assertEquals("SELECT id,name FROM users;", query);
    }

    @Test
    void testWhereClauseWithEqualCondition() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .where(Map.of("id", Condition.equal(5)))
                .buildToString();
        assertEquals("SELECT * FROM users WHERE id = 5;", query);
    }

    @Test
    void testWhereClauseWithNullValue() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .where(Map.of("id", Condition.beginLike(null)))
                .buildToString();
        assertEquals("SELECT * FROM users;", query);
    }

    @Test
    void testWhereClauseWithMultipleConditions() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .where(Map.of(
                        "id", Condition.equal(5),
                        "phone", Condition.equal(null),
                        "age", Condition.between(15, 25),
                        "name", Condition.anyLike("John's")
                ))
                .buildToString();
        assertFalse(query.contains("phone"));
        assertTrue(query.contains("name LIKE '%John''s%'"));
    }

    @Test
    void testOrderBy() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .orderBy("name", Order.ASCENDING)
                .buildToString();
        assertEquals("SELECT * FROM users ORDER BY name ASC;", query);
    }

    @Test
    void testGroupBy() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .groupBy("role")
                .buildToString();
        assertEquals("SELECT * FROM users GROUP BY role;", query);
    }

    @Test
    void testHaving() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .groupBy("role")
                .having("COUNT(id) > 1")
                .buildToString();
        assertEquals("SELECT * FROM users GROUP BY role HAVING COUNT(id) > 1;", query);
    }

    @Test
    void testJoin() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .join(JoinType.INNER, "orders", "users.id = orders.user_id")
                .buildToString();
        assertEquals("SELECT * FROM users INNER JOIN orders ON users.id = orders.user_id;", query);
    }

    @Test
    void testSubquery() {
        String query = DynamicQueryBuilder.of()
                .subquery("sub", "SELECT * FROM users")
                .buildToString();
        assertEquals("(SELECT * FROM users) sub;", query);
    }

    @Test
    void testDistinct() {
        String query = DynamicQueryBuilder.of()
                .selectSpecificColumnsFrom("users", "name")
                .distinct()
                .buildToString();
        assertEquals("SELECT DISTINCT name FROM users;", query);
    }

    @Test
    void testAlias() {
        String query = DynamicQueryBuilder.of()
                .selectSpecificColumnsFrom("users", "id")
                .alias("user_id")
                .buildToString();
        assertEquals("SELECT id FROM users AS user_id;", query);
    }

    @Test
    void testLimit() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .limit(10)
                .buildToString();
        assertEquals("SELECT * FROM users LIMIT 10;", query);
    }

    @Test
    void testOffset() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .offset(20)
                .buildToString();
        assertEquals("SELECT * FROM users OFFSET 20;", query);
    }

    @Test
    void testBuildPrettyString() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .where(Map.of("id", Condition.equal(5)))
                .buildPrettyString();
        assertTrue(query.contains("SELECT"));
        assertTrue(query.contains("WHERE"));
        assertTrue(query.contains("id = 5"));
    }

    @Test
    void testWhereWithDateCondition() {
        LocalDate date = LocalDate.of(2024, 3, 20);
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .where(Map.of("created_at", Condition.equal(date)))
                .buildToString();
        assertEquals("SELECT * FROM users WHERE created_at = '2024-03-20';", query);
    }

    @Test
    void testWhereWithDateTimeCondition() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 20, 12, 0);
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .where(Map.of("created_at", Condition.equal(dateTime)))
                .buildToString();
        assertEquals("SELECT * FROM users WHERE created_at = '2024-03-20 12:00:00';", query);
    }

    @Test
    void testSanitizeTableNameAndColumnName() {
        String query = DynamicQueryBuilder.of()
                .selectSpecificColumnsFrom("users; DROP TABLE users;", "name", "email", "password")
                .buildToString();

        assertFalse(query.contains("DROP TABLE"));
        assertTrue(query.contains("users"));
    }

    @Test
    void testSanitizeWhereClauseInjectionAttempt() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .where(Map.of(
                        "id", Condition.equal(1),
                        "name", Condition.anyLike("John'; DROP TABLE users; --")
                ))
                .buildToString();

        assertFalse(query.contains("DROP TABLE"));
        assertFalse(query.contains("--"));
        assertTrue(query.contains("name LIKE '%John''%'"));
    }

    @Test
    void testSanitizeJoinClause() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("users")
                .join(JoinType.INNER, "orders; DROP TABLE orders;", "users.id = orders.user_id")
                .buildToString();

        assertFalse(query.contains("DROP TABLE"));
        assertTrue(query.contains("INNER JOIN orders ON users.id = orders.user_id"));
    }

    @Test
    void testSanitizeHavingClause() {
        String query = DynamicQueryBuilder.of()
                .selectFrom("transactions")
                .groupBy("status")
                .having("SUM(amount) > 1000; DELETE FROM transactions;")
                .buildToString();

        assertFalse(query.contains("DELETE FROM"));
        assertTrue(query.contains("HAVING SUM(amount) > 1000"));
    }

}