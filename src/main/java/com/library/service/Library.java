package com.library.service;

import com.library.exception.BorrowLimitExceededException;
import com.library.exception.DuplicateItemException;
import com.library.exception.DuplicatePatronException;
import com.library.exception.ItemNotAvailableException;
import com.library.exception.ItemNotBorrowedException;
import com.library.exception.ItemNotFoundException;
import com.library.exception.OutstandingFineException;
import com.library.exception.PatronNotFoundException;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Patron;
import com.library.repository.ItemRepository;
import com.library.repository.LoanRepository;
import com.library.repository.PatronRepository;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Library {

    private final ItemRepository itemRepository;
    private final PatronRepository patronRepository;
    private final LoanRepository loanRepository;

    public Library(ItemRepository itemRepository, PatronRepository patronRepository, LoanRepository loanRepository) {
        if (itemRepository == null || patronRepository == null || loanRepository == null) {
            throw new IllegalArgumentException("Repositories must not be null");
        }
        this.itemRepository = itemRepository;
        this.patronRepository = patronRepository;
        this.loanRepository = loanRepository;
    }

    public void addItem(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        if (itemRepository.existsById(item.getId())) {
            throw new DuplicateItemException("Item " + item.getId() + " already exists");
        }
        itemRepository.save(item);
    }

    public void registerPatron(Patron patron) {
        if (patron == null) {
            throw new IllegalArgumentException("Patron must not be null");
        }
        if (patronRepository.existsById(patron.getId())) {
            throw new DuplicatePatronException("Patron " + patron.getId() + " already exists");
        }
        patronRepository.save(patron);
    }

    public Loan borrow(String patronId, String itemId, LocalDate date) {
        Patron patron = findPatron(patronId);
        LibraryItem item = findItem(itemId);
        if (patron.hasOutstandingFine()) {
            throw new OutstandingFineException("Patron " + patronId + " has an outstanding fine");
        }
        if (loanRepository.findActiveByItemId(itemId).isPresent()) {
            throw new ItemNotAvailableException("Item " + itemId + " is already borrowed");
        }
        if (loanRepository.countActiveByPatronId(patronId) >= patron.getPolicy().getBorrowLimit()) {
            throw new BorrowLimitExceededException("Patron " + patronId + " reached the borrow limit");
        }
        Loan loan = new Loan(item, patron, date);
        loanRepository.save(loan);
        return loan;
    }

    public Loan returnItem(String itemId, LocalDate date) {
        Loan loan = findActiveLoan(itemId);
        loan.markReturned(date);
        BigDecimal fine = loan.calculateFine(date);
        if (fine.signum() > 0) {
            loan.getPatron().addFine(fine);
        }
        return loan;
    }

    public BigDecimal calculateFine(String itemId, LocalDate date) {
        return findActiveLoan(itemId).calculateFine(date);
    }

    public void payFine(String patronId, BigDecimal amount) {
        findPatron(patronId).payFine(amount);
    }

    private Patron findPatron(String patronId) {
        return patronRepository.findById(patronId)
                .orElseThrow(() -> new PatronNotFoundException("Patron " + patronId + " not found"));
    }

    private LibraryItem findItem(String itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item " + itemId + " not found"));
    }

    private Loan findActiveLoan(String itemId) {
        findItem(itemId);
        return loanRepository.findActiveByItemId(itemId)
                .orElseThrow(() -> new ItemNotBorrowedException("Item " + itemId + " is not borrowed"));
    }
}
