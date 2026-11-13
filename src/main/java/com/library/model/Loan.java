package com.library.model;

import com.library.exception.InvalidDateException;
import com.library.exception.ItemNotBorrowedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Loan {

    private final LibraryItem item;
    private final Patron patron;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(LibraryItem item, Patron patron, LocalDate borrowDate) {
        if (item == null || patron == null || borrowDate == null) {
            throw new IllegalArgumentException("Item, patron and borrow date must not be null");
        }
        this.item = item;
        this.patron = patron;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(item.getLoanPeriodDays());
    }

    public LibraryItem getItem() {
        return item;
    }

    public Patron getPatron() {
        return patron;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Optional<LocalDate> getReturnDate() {
        return Optional.ofNullable(returnDate);
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate date) {
        requireNotBeforeBorrowDate(date);
        if (isReturned()) {
            throw new ItemNotBorrowedException("Item " + item.getId() + " is already returned");
        }
        returnDate = date;
    }

    public BigDecimal calculateFine(LocalDate date) {
        requireNotBeforeBorrowDate(date);
        long overdueDays = Math.max(0, ChronoUnit.DAYS.between(dueDate, date));
        return patron.getPolicy().getDailyFine().multiply(BigDecimal.valueOf(overdueDays));
    }

    private void requireNotBeforeBorrowDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        if (date.isBefore(borrowDate)) {
            throw new InvalidDateException("Date " + date + " is before borrow date " + borrowDate);
        }
    }
}
