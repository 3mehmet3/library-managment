package com.library.service;

import com.library.exception.DuplicateItemException;
import com.library.exception.DuplicatePatronException;
import com.library.model.LibraryItem;
import com.library.model.Patron;
import com.library.repository.ItemRepository;
import com.library.repository.LoanRepository;
import com.library.repository.PatronRepository;

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
}
