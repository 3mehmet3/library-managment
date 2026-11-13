package com.library.policy;

import java.math.BigDecimal;

public class FacultyPolicy implements PatronPolicy {

    @Override
    public int getBorrowLimit() {
        return 10;
    }

    @Override
    public BigDecimal getDailyFine() {
        return new BigDecimal("0.50");
    }
}
