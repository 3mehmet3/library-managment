package com.library.policy;

import java.math.BigDecimal;

public class StudentPolicy implements PatronPolicy {

    @Override
    public int getBorrowLimit() {
        return 3;
    }

    @Override
    public BigDecimal getDailyFine() {
        return new BigDecimal("1.00");
    }
}
