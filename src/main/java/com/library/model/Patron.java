package com.library.model;

import com.library.exception.InvalidPaymentException;
import com.library.policy.PatronPolicy;
import java.math.BigDecimal;

public class Patron {

    private final String id;
    private final String name;
    private final PatronPolicy policy;
    private BigDecimal fineBalance = BigDecimal.ZERO;

    public Patron(String id, String name, PatronPolicy policy) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Patron id must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Patron name must not be blank");
        }
        if (policy == null) {
            throw new IllegalArgumentException("Patron policy must not be null");
        }
        this.id = id;
        this.name = name;
        this.policy = policy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PatronPolicy getPolicy() {
        return policy;
    }

    public BigDecimal getFineBalance() {
        return fineBalance;
    }

    public boolean hasOutstandingFine() {
        return fineBalance.signum() > 0;
    }

    public void addFine(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Fine amount must not be negative");
        }
        fineBalance = fineBalance.add(amount);
    }

    public void payFine(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Payment amount must not be null");
        }
        if (amount.signum() <= 0) {
            throw new InvalidPaymentException("Payment amount must be positive");
        }
        if (amount.compareTo(fineBalance) > 0) {
            throw new InvalidPaymentException("Payment amount exceeds fine balance");
        }
        fineBalance = fineBalance.subtract(amount);
    }
}
