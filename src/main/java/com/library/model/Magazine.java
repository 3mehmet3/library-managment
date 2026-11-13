package com.library.model;

public class Magazine extends LibraryItem {

    public Magazine(String id, String title) {
        super(id, title);
    }

    @Override
    public int getLoanPeriodDays() {
        return 3;
    }
}
