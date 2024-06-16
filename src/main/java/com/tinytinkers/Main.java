package com.tinytinkers;

import java.util.List;
import java.util.Map;

import static com.tinytinkers.Condition.*;
import static com.tinytinkers.Condition.equal;
import static com.tinytinkers.DynamicQueryBuilderUtil.dynamicQueryBuilder;

public class Main {
    public static void main(String[] args) {
        System.out.println(
                dynamicQueryBuilder()
                        .selectFrom("user")
                        .where(Map.of(
                                "first_name", like("John"),
                                "last_name", like("Doe"),
                                "age", between(18, 45),
                                "roles", in(List.of("CREATOR", "VERIFIER")),
                                "lucky_numbers", in(List.of(7, 8, 9, 18, 108)),
                                "user_name", equal("john_doe"),
                                "status", equal("ACTIVE")))
                        .orderBy("id", DynamicQueryBuilderUtil.Order.DESCENDING)
                        .buildPrettyString()
        );
    }
}