package com.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.exception.InvalidDateException;
import com.library.exception.ItemNotBorrowedException;
import com.library.policy.FacultyPolicy;
import com.library.policy.StudentPolicy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoanTest {

    private static final LocalDate BORROW_DATE = LocalDate.of(2026, 3, 1);
    private static final LocalDate DUE_DATE = LocalDate.of(2026, 3, 22);

    private LibraryItem book;
    private Patron student;
    private Loan loan;

    @BeforeEach
    void setUp() {
        book = new Book("b1", "Clean Code");
        student = new Patron("p1", "Ali", new StudentPolicy());
        loan = new Loan(book, student, BORROW_DATE);
    }

    @Test
    void constructor_book_setsDueDate21DaysAfterBorrowDate() {
        assertEquals(book, loan.getItem());
        assertEquals(student, loan.getPatron());
        assertEquals(BORROW_DATE, loan.getBorrowDate());
        assertEquals(DUE_DATE, loan.getDueDate());
    }

    @Test
    void constructor_dvd_setsDueDate7DaysAfterBorrowDate() {
        Loan dvdLoan = new Loan(new Dvd("d1", "Inception"), student, BORROW_DATE);

        assertEquals(LocalDate.of(2026, 3, 8), dvdLoan.getDueDate());
    }

    @Test
    void constructor_nullBorrowDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Loan(book, student, null));
    }

    @Test
    void getReturnDate_notReturned_returnsEmpty() {
        assertEquals(Optional.empty(), loan.getReturnDate());
        assertFalse(loan.isReturned());
    }

    @Test
    void markReturned_validDate_setsReturnDate() {
        loan.markReturned(DUE_DATE);

        assertEquals(Optional.of(DUE_DATE), loan.getReturnDate());
        assertTrue(loan.isReturned());
    }

    @Test
    void markReturned_dateBeforeBorrowDate_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> loan.markReturned(BORROW_DATE.minusDays(1)));
        assertFalse(loan.isReturned());
    }

    @Test
    void markReturned_alreadyReturned_throwsItemNotBorrowedException() {
        loan.markReturned(DUE_DATE);

        assertThrows(ItemNotBorrowedException.class, () -> loan.markReturned(DUE_DATE.plusDays(1)));
        assertEquals(Optional.of(DUE_DATE), loan.getReturnDate());
    }

    @Test
    void calculateFine_onDueDate_returnsZero() {
        assertEquals(0, loan.calculateFine(DUE_DATE).signum());
    }

    @Test
    void calculateFine_beforeDueDate_returnsZero() {
        assertEquals(0, loan.calculateFine(BORROW_DATE).signum());
    }

    @Test
    void calculateFine_studentFiveDaysLate_returnsFive() {
        assertEquals(new BigDecimal("5.00"), loan.calculateFine(DUE_DATE.plusDays(5)));
    }

    @Test
    void calculateFine_facultyFiveDaysLate_returnsTwoAndHalf() {
        Patron faculty = new Patron("p2", "Ayse", new FacultyPolicy());
        Loan facultyLoan = new Loan(book, faculty, BORROW_DATE);

        assertEquals(new BigDecimal("2.50"), facultyLoan.calculateFine(DUE_DATE.plusDays(5)));
    }

    @Test
    void calculateFine_dateBeforeBorrowDate_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> loan.calculateFine(BORROW_DATE.minusDays(1)));
    }
}
