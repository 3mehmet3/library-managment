package com.library.repository;

import com.library.model.Loan;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLoanRepository implements LoanRepository {

    private final List<Loan> loans = new ArrayList<>();

    @Override
    public void save(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan must not be null");
        }
        loans.add(loan);
    }

    @Override
    public Optional<Loan> findActiveByItemId(String itemId) {
        return loans.stream()
                .filter(loan -> !loan.isReturned())
                .filter(loan -> loan.getItem().getId().equals(itemId))
                .findFirst();
    }

    @Override
    public int countActiveByPatronId(String patronId) {
        return (int) loans.stream()
                .filter(loan -> !loan.isReturned())
                .filter(loan -> loan.getPatron().getId().equals(patronId))
                .count();
    }
}
