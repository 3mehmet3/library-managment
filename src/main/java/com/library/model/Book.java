package com.library.model;

public class Book extends LibraryItem {

    public Book(String id, String title) {
        super(id, title);
    }

    @Override
    public int getLoanPeriodDays() {
        return 21;
    }
}
