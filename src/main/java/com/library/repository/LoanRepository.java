package com.library.repository;

import com.library.model.Loan;
import java.util.Optional;

public interface LoanRepository {

    void save(Loan loan);

    Optional<Loan> findActiveByItemId(String itemId);

    int countActiveByPatronId(String patronId);
}
