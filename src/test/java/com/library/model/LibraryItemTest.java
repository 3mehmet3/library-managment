package com.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LibraryItemTest {

    @Test
    void getLoanPeriodDays_book_returns21() {
        assertEquals(21, new Book("b1", "Clean Code").getLoanPeriodDays());
    }

    @Test
    void getLoanPeriodDays_dvd_returns7() {
        assertEquals(7, new Dvd("d1", "Inception").getLoanPeriodDays());
    }

    @Test
    void getLoanPeriodDays_magazine_returns3() {
        assertEquals(3, new Magazine("m1", "Wired").getLoanPeriodDays());
    }

    @Test
    void constructor_validArguments_storesIdAndTitle() {
        LibraryItem item = new Book("b1", "Clean Code");

        assertEquals("b1", item.getId());
        assertEquals("Clean Code", item.getTitle());
    }

    @Test
    void constructor_nullId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Book(null, "Clean Code"));
    }

    @Test
    void constructor_blankId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Dvd(" ", "Inception"));
    }

    @Test
    void constructor_nullTitle_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Magazine("m1", null));
    }

    @Test
    void constructor_blankTitle_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Book("b1", ""));
    }
}
