package com.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.exception.BorrowLimitExceededException;
import com.library.exception.DuplicateItemException;
import com.library.exception.DuplicatePatronException;
import com.library.exception.InvalidDateException;
import com.library.exception.InvalidPaymentException;
import com.library.exception.ItemNotAvailableException;
import com.library.exception.ItemNotBorrowedException;
import com.library.exception.ItemNotFoundException;
import com.library.exception.OutstandingFineException;
import com.library.exception.PatronNotFoundException;
import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Patron;
import com.library.policy.FacultyPolicy;
import com.library.policy.StudentPolicy;
import com.library.repository.InMemoryItemRepository;
import com.library.repository.InMemoryLoanRepository;
import com.library.repository.InMemoryPatronRepository;
import com.library.repository.ItemRepository;
import com.library.repository.LoanRepository;
import com.library.repository.PatronRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryTest {

    private static final LocalDate BORROW_DATE = LocalDate.of(2026, 3, 1);

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

    @Test
    void borrow_availableItem_returnsActiveLoan() {
        Patron student = registerStudent();
        LibraryItem book = addBook("b1");

        Loan loan = library.borrow("p1", "b1", BORROW_DATE);

        assertEquals(book, loan.getItem());
        assertEquals(student, loan.getPatron());
        assertEquals(BORROW_DATE, loan.getBorrowDate());
        assertEquals(BORROW_DATE.plusDays(21), loan.getDueDate());
        assertEquals(Optional.of(loan), loanRepository.findActiveByItemId("b1"));
    }

    @Test
    void borrow_unknownPatron_throwsPatronNotFoundException() {
        addBook("b1");

        assertThrows(PatronNotFoundException.class, () -> library.borrow("missing", "b1", BORROW_DATE));
    }

    @Test
    void borrow_unknownItem_throwsItemNotFoundException() {
        registerStudent();

        assertThrows(ItemNotFoundException.class, () -> library.borrow("p1", "missing", BORROW_DATE));
    }

    @Test
    void borrow_patronWithOutstandingFine_throwsOutstandingFineException() {
        Patron student = registerStudent();
        addBook("b1");
        student.addFine(new BigDecimal("1.00"));

        assertThrows(OutstandingFineException.class, () -> library.borrow("p1", "b1", BORROW_DATE));
        assertEquals(Optional.empty(), loanRepository.findActiveByItemId("b1"));
    }

    @Test
    void borrow_itemAlreadyBorrowed_throwsItemNotAvailableException() {
        registerStudent();
        library.registerPatron(new Patron("p2", "Ayse", new FacultyPolicy()));
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);

        assertThrows(ItemNotAvailableException.class, () -> library.borrow("p2", "b1", BORROW_DATE));
    }

    @Test
    void borrow_studentAtBorrowLimit_throwsBorrowLimitExceededException() {
        registerStudent();
        for (String id : List.of("b1", "b2", "b3", "b4")) {
            addBook(id);
        }
        library.borrow("p1", "b1", BORROW_DATE);
        library.borrow("p1", "b2", BORROW_DATE);
        library.borrow("p1", "b3", BORROW_DATE);

        assertThrows(BorrowLimitExceededException.class, () -> library.borrow("p1", "b4", BORROW_DATE));
        assertEquals(3, loanRepository.countActiveByPatronId("p1"));
    }

    @Test
    void borrow_afterReturningAtLimit_succeeds() {
        registerStudent();
        for (String id : List.of("b1", "b2", "b3", "b4")) {
            addBook(id);
        }
        library.borrow("p1", "b1", BORROW_DATE);
        library.borrow("p1", "b2", BORROW_DATE);
        library.borrow("p1", "b3", BORROW_DATE);
        library.returnItem("b1", BORROW_DATE.plusDays(1));

        library.borrow("p1", "b4", BORROW_DATE.plusDays(1));

        assertEquals(3, loanRepository.countActiveByPatronId("p1"));
    }

    @Test
    void returnItem_onTime_marksLoanReturnedWithoutFine() {
        Patron student = registerStudent();
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);

        Loan loan = library.returnItem("b1", BORROW_DATE.plusDays(21));

        assertTrue(loan.isReturned());
        assertEquals(Optional.of(BORROW_DATE.plusDays(21)), loan.getReturnDate());
        assertFalse(student.hasOutstandingFine());
        assertEquals(Optional.empty(), loanRepository.findActiveByItemId("b1"));
    }

    @Test
    void returnItem_late_addsFineToPatron() {
        Patron student = registerStudent();
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);

        library.returnItem("b1", BORROW_DATE.plusDays(24));

        assertEquals(new BigDecimal("3.00"), student.getFineBalance());
    }

    @Test
    void returnItem_returnedItem_canBeBorrowedAgain() {
        registerStudent();
        library.registerPatron(new Patron("p2", "Ayse", new FacultyPolicy()));
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);
        library.returnItem("b1", BORROW_DATE.plusDays(2));

        Loan loan = library.borrow("p2", "b1", BORROW_DATE.plusDays(2));

        assertEquals("p2", loan.getPatron().getId());
    }

    @Test
    void returnItem_unknownItem_throwsItemNotFoundException() {
        assertThrows(ItemNotFoundException.class, () -> library.returnItem("missing", BORROW_DATE));
    }

    @Test
    void returnItem_itemNotBorrowed_throwsItemNotBorrowedException() {
        addBook("b1");

        assertThrows(ItemNotBorrowedException.class, () -> library.returnItem("b1", BORROW_DATE));
    }

    @Test
    void returnItem_dateBeforeBorrowDate_throwsInvalidDateException() {
        registerStudent();
        addBook("b1");
        Loan loan = library.borrow("p1", "b1", BORROW_DATE);

        assertThrows(InvalidDateException.class, () -> library.returnItem("b1", BORROW_DATE.minusDays(1)));
        assertFalse(loan.isReturned());
    }

    @Test
    void calculateFine_beforeDueDate_returnsZero() {
        registerStudent();
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);

        assertEquals(0, library.calculateFine("b1", BORROW_DATE.plusDays(21)).signum());
    }

    @Test
    void calculateFine_studentFourDaysLate_returnsFour() {
        registerStudent();
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);

        assertEquals(new BigDecimal("4.00"), library.calculateFine("b1", BORROW_DATE.plusDays(25)));
    }

    @Test
    void calculateFine_facultyFourDaysLate_returnsTwo() {
        library.registerPatron(new Patron("p2", "Ayse", new FacultyPolicy()));
        addBook("b1");
        library.borrow("p2", "b1", BORROW_DATE);

        assertEquals(new BigDecimal("2.00"), library.calculateFine("b1", BORROW_DATE.plusDays(25)));
    }

    @Test
    void calculateFine_activeLoan_doesNotChangePatronBalance() {
        Patron student = registerStudent();
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);

        library.calculateFine("b1", BORROW_DATE.plusDays(25));

        assertFalse(student.hasOutstandingFine());
    }

    @Test
    void calculateFine_unknownItem_throwsItemNotFoundException() {
        assertThrows(ItemNotFoundException.class, () -> library.calculateFine("missing", BORROW_DATE));
    }

    @Test
    void calculateFine_itemNotBorrowed_throwsItemNotBorrowedException() {
        addBook("b1");

        assertThrows(ItemNotBorrowedException.class, () -> library.calculateFine("b1", BORROW_DATE));
    }

    @Test
    void calculateFine_dateBeforeBorrowDate_throwsInvalidDateException() {
        registerStudent();
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);

        assertThrows(InvalidDateException.class, () -> library.calculateFine("b1", BORROW_DATE.minusDays(1)));
    }

    @Test
    void payFine_fullAmount_allowsBorrowingAgain() {
        Patron student = registerStudent();
        addBook("b1");
        library.borrow("p1", "b1", BORROW_DATE);
        library.returnItem("b1", BORROW_DATE.plusDays(24));

        library.payFine("p1", new BigDecimal("3.00"));

        assertFalse(student.hasOutstandingFine());
        assertTrue(library.borrow("p1", "b1", BORROW_DATE.plusDays(24)).getReturnDate().isEmpty());
    }

    @Test
    void payFine_partialAmount_keepsRemainingBalance() {
        Patron student = registerStudent();
        student.addFine(new BigDecimal("3.00"));

        library.payFine("p1", new BigDecimal("1.00"));

        assertEquals(new BigDecimal("2.00"), student.getFineBalance());
    }

    @Test
    void payFine_unknownPatron_throwsPatronNotFoundException() {
        assertThrows(PatronNotFoundException.class, () -> library.payFine("missing", new BigDecimal("1.00")));
    }

    @Test
    void payFine_amountGreaterThanBalance_throwsInvalidPaymentException() {
        Patron student = registerStudent();
        student.addFine(new BigDecimal("3.00"));

        assertThrows(InvalidPaymentException.class, () -> library.payFine("p1", new BigDecimal("3.50")));
        assertEquals(new BigDecimal("3.00"), student.getFineBalance());
    }

    @Test
    void payFine_zeroAmount_throwsInvalidPaymentException() {
        Patron student = registerStudent();
        student.addFine(new BigDecimal("3.00"));

        assertThrows(InvalidPaymentException.class, () -> library.payFine("p1", BigDecimal.ZERO));
    }

    private Patron registerStudent() {
        Patron student = new Patron("p1", "Ali", new StudentPolicy());
        library.registerPatron(student);
        return student;
    }

    private LibraryItem addBook(String id) {
        LibraryItem book = new Book(id, "Clean Code");
        library.addItem(book);
        return book;
    }
}
