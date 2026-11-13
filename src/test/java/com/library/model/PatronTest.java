package com.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.exception.InvalidPaymentException;
import com.library.policy.StudentPolicy;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatronTest {

    private Patron patron;

    @BeforeEach
    void setUp() {
        patron = new Patron("p1", "Ali", new StudentPolicy());
    }

    @Test
    void constructor_validArguments_startsWithZeroBalance() {
        assertEquals("p1", patron.getId());
        assertEquals("Ali", patron.getName());
        assertEquals(3, patron.getPolicy().getBorrowLimit());
        assertEquals(BigDecimal.ZERO, patron.getFineBalance());
        assertFalse(patron.hasOutstandingFine());
    }

    @Test
    void constructor_blankId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Patron("", "Ali", new StudentPolicy()));
    }

    @Test
    void constructor_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Patron("p1", null, new StudentPolicy()));
    }

    @Test
    void constructor_nullPolicy_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Patron("p1", "Ali", null));
    }

    @Test
    void addFine_positiveAmount_increasesBalance() {
        patron.addFine(new BigDecimal("2.00"));
        patron.addFine(new BigDecimal("1.50"));

        assertEquals(new BigDecimal("3.50"), patron.getFineBalance());
        assertTrue(patron.hasOutstandingFine());
    }

    @Test
    void addFine_negativeAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> patron.addFine(new BigDecimal("-1.00")));
    }

    @Test
    void payFine_partialAmount_decreasesBalance() {
        patron.addFine(new BigDecimal("5.00"));

        patron.payFine(new BigDecimal("2.00"));

        assertEquals(new BigDecimal("3.00"), patron.getFineBalance());
        assertTrue(patron.hasOutstandingFine());
    }

    @Test
    void payFine_fullAmount_clearsOutstandingFine() {
        patron.addFine(new BigDecimal("5.00"));

        patron.payFine(new BigDecimal("5.00"));

        assertFalse(patron.hasOutstandingFine());
    }

    @Test
    void payFine_zeroAmount_throwsInvalidPaymentException() {
        patron.addFine(new BigDecimal("5.00"));

        assertThrows(InvalidPaymentException.class, () -> patron.payFine(BigDecimal.ZERO));
    }

    @Test
    void payFine_negativeAmount_throwsInvalidPaymentException() {
        patron.addFine(new BigDecimal("5.00"));

        assertThrows(InvalidPaymentException.class, () -> patron.payFine(new BigDecimal("-1.00")));
    }

    @Test
    void payFine_amountGreaterThanBalance_throwsInvalidPaymentException() {
        patron.addFine(new BigDecimal("5.00"));

        assertThrows(InvalidPaymentException.class, () -> patron.payFine(new BigDecimal("5.01")));
        assertEquals(new BigDecimal("5.00"), patron.getFineBalance());
    }

    @Test
    void payFine_nullAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> patron.payFine(null));
    }
}
