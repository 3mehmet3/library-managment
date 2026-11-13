package com.library.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PatronPolicyTest {

    @Test
    void getBorrowLimit_student_returns3() {
        assertEquals(3, new StudentPolicy().getBorrowLimit());
    }

    @Test
    void getDailyFine_student_returnsOne() {
        assertEquals(new BigDecimal("1.00"), new StudentPolicy().getDailyFine());
    }

    @Test
    void getBorrowLimit_faculty_returns10() {
        assertEquals(10, new FacultyPolicy().getBorrowLimit());
    }

    @Test
    void getDailyFine_faculty_returnsHalf() {
        assertEquals(new BigDecimal("0.50"), new FacultyPolicy().getDailyFine());
    }
}
