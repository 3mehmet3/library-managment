package com.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

class LibraryItemTest {

    @ParameterizedTest
    @MethodSource("loanPeriods")
    void getLoanPeriodDays_matchesItemType(LibraryItem item, int days) {
        assertEquals(days, item.getLoanPeriodDays());
    }

    static Stream<Arguments> loanPeriods() {
        return Stream.of(
                Arguments.of(new Book("b1", "Clean Code"), 21),
                Arguments.of(new Dvd("d1", "Inception"), 7),
                Arguments.of(new Magazine("m1", "Wired"), 3));
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
