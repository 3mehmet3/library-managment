package com.library.policy;

import java.math.BigDecimal;

public interface PatronPolicy {

    int getBorrowLimit();

    BigDecimal getDailyFine();
}
