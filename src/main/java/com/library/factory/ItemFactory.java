package com.library.factory;

import com.library.model.Book;
import com.library.model.Dvd;
import com.library.model.ItemType;
import com.library.model.LibraryItem;
import com.library.model.Magazine;

public class ItemFactory {

    public LibraryItem create(ItemType type, String id, String title) {
        if (type == null) {
            throw new IllegalArgumentException("Item type must not be null");
        }
        return switch (type) {
            case BOOK -> new Book(id, title);
            case DVD -> new Dvd(id, title);
            case MAGAZINE -> new Magazine(id, title);
        };
    }
}
