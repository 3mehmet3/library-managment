package com.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.library.exception.DuplicateItemException;
import com.library.exception.DuplicatePatronException;
import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Patron;
import com.library.policy.FacultyPolicy;
import com.library.policy.StudentPolicy;
import com.library.repository.InMemoryItemRepository;
import com.library.repository.InMemoryLoanRepository;
import com.library.repository.InMemoryPatronRepository;
import com.library.repository.ItemRepository;
import com.library.repository.LoanRepository;
import com.library.repository.PatronRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryTest {

    private ItemRepository itemRepository;
    private PatronRepository patronRepository;
    private LoanRepository loanRepository;
    private Library library;

    @BeforeEach
    void setUp() {
        itemRepository = new InMemoryItemRepository();
        patronRepository = new InMemoryPatronRepository();
        loanRepository = new InMemoryLoanRepository();
        library = new Library(itemRepository, patronRepository, loanRepository);
    }

    @Test
    void constructor_nullRepository_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Library(itemRepository, patronRepository, null));
    }

    @Test
    void addItem_newItem_savesItem() {
        LibraryItem book = new Book("b1", "Clean Code");

        library.addItem(book);

        assertEquals(Optional.of(book), itemRepository.findById("b1"));
    }

    @Test
    void addItem_duplicateId_throwsDuplicateItemException() {
        LibraryItem book = new Book("b1", "Clean Code");
        library.addItem(book);

        assertThrows(DuplicateItemException.class, () -> library.addItem(new Dvd("b1", "Inception")));
        assertEquals(Optional.of(book), itemRepository.findById("b1"));
    }

    @Test
    void addItem_nullItem_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.addItem(null));
    }

    @Test
    void registerPatron_newPatron_savesPatron() {
        Patron patron = new Patron("p1", "Ali", new StudentPolicy());

        library.registerPatron(patron);

        assertEquals(Optional.of(patron), patronRepository.findById("p1"));
    }

    @Test
    void registerPatron_duplicateId_throwsDuplicatePatronException() {
        Patron patron = new Patron("p1", "Ali", new StudentPolicy());
        library.registerPatron(patron);

        assertThrows(DuplicatePatronException.class, () -> library.registerPatron(new Patron("p1", "Ayse", new FacultyPolicy())));
        assertEquals(Optional.of(patron), patronRepository.findById("p1"));
    }

    @Test
    void registerPatron_nullPatron_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.registerPatron(null));
    }
}
