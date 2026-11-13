package com.library.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.ItemType;
import com.library.model.LibraryItem;
import com.library.model.Magazine;
import org.junit.jupiter.api.Test;

class ItemFactoryTest {

    private final ItemFactory factory = new ItemFactory();

    @Test
    void create_bookType_returnsBook() {
        LibraryItem item = factory.create(ItemType.BOOK, "b1", "Clean Code");

        assertInstanceOf(Book.class, item);
        assertEquals("b1", item.getId());
        assertEquals("Clean Code", item.getTitle());
    }

    @Test
    void create_dvdType_returnsDvd() {
        assertInstanceOf(Dvd.class, factory.create(ItemType.DVD, "d1", "Inception"));
    }

    @Test
    void create_magazineType_returnsMagazine() {
        assertInstanceOf(Magazine.class, factory.create(ItemType.MAGAZINE, "m1", "Wired"));
    }

    @Test
    void create_nullType_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> factory.create(null, "b1", "Clean Code"));
    }

    @Test
    void create_blankId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> factory.create(ItemType.BOOK, "", "Clean Code"));
    }

    @Test
    void create_nullTitle_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> factory.create(ItemType.DVD, "d1", null));
    }
}
