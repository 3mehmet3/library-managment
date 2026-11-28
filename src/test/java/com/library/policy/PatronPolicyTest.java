package com.library.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

class PatronPolicyTest {

    @ParameterizedTest
    @MethodSource("policies")
    void policy_hasExpectedLimitAndFine(PatronPolicy policy, int limit, BigDecimal fine) {
        assertEquals(limit, policy.getBorrowLimit());
        assertEquals(fine, policy.getDailyFine());
    }

    static Stream<Arguments> policies() {
        return Stream.of(
                Arguments.of(new StudentPolicy(), 3, new BigDecimal("1.00")),
                Arguments.of(new FacultyPolicy(), 10, new BigDecimal("0.50")));
    }
}
