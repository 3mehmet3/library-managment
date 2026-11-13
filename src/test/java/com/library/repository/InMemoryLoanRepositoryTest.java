package com.library.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.Loan;
import com.library.model.Patron;
import com.library.policy.StudentPolicy;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryLoanRepositoryTest {

    private static final LocalDate BORROW_DATE = LocalDate.of(2026, 3, 1);

    private LoanRepository repository;
    private Patron patron;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLoanRepository();
        patron = new Patron("p1", "Ali", new StudentPolicy());
    }

    @Test
    void findActiveByItemId_activeLoan_returnsLoan() {
        Loan loan = new Loan(new Book("b1", "Clean Code"), patron, BORROW_DATE);
        repository.save(loan);

        assertEquals(Optional.of(loan), repository.findActiveByItemId("b1"));
    }

    @Test
    void findActiveByItemId_returnedLoan_returnsEmpty() {
        Loan loan = new Loan(new Book("b1", "Clean Code"), patron, BORROW_DATE);
        repository.save(loan);
        loan.markReturned(BORROW_DATE.plusDays(2));

        assertEquals(Optional.empty(), repository.findActiveByItemId("b1"));
    }

    @Test
    void findActiveByItemId_unknownItem_returnsEmpty() {
        assertEquals(Optional.empty(), repository.findActiveByItemId("missing"));
    }

    @Test
    void countActiveByPatronId_twoActiveLoans_returnsTwo() {
        repository.save(new Loan(new Book("b1", "Clean Code"), patron, BORROW_DATE));
        repository.save(new Loan(new Dvd("d1", "Inception"), patron, BORROW_DATE));

        assertEquals(2, repository.countActiveByPatronId("p1"));
    }

    @Test
    void countActiveByPatronId_returnedLoan_isNotCounted() {
        Loan returned = new Loan(new Book("b1", "Clean Code"), patron, BORROW_DATE);
        repository.save(returned);
        repository.save(new Loan(new Dvd("d1", "Inception"), patron, BORROW_DATE));
        returned.markReturned(BORROW_DATE.plusDays(2));

        assertEquals(1, repository.countActiveByPatronId("p1"));
    }

    @Test
    void countActiveByPatronId_otherPatronsLoans_areNotCounted() {
        Patron other = new Patron("p2", "Ayse", new StudentPolicy());
        repository.save(new Loan(new Book("b1", "Clean Code"), other, BORROW_DATE));

        assertEquals(0, repository.countActiveByPatronId("p1"));
    }
}
