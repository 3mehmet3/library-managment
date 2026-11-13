package com.library.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.model.Book;
import com.library.model.LibraryItem;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryItemRepositoryTest {

    private final ItemRepository repository = new InMemoryItemRepository();

    @Test
    void findById_savedItem_returnsItem() {
        LibraryItem book = new Book("b1", "Clean Code");
        repository.save(book);

        assertEquals(Optional.of(book), repository.findById("b1"));
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        assertEquals(Optional.empty(), repository.findById("missing"));
    }

    @Test
    void existsById_savedItem_returnsTrue() {
        repository.save(new Book("b1", "Clean Code"));

        assertTrue(repository.existsById("b1"));
    }

    @Test
    void existsById_unknownId_returnsFalse() {
        assertFalse(repository.existsById("missing"));
    }
}
