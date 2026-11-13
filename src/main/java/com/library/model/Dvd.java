package com.library.model;

public class Dvd extends LibraryItem {

    public Dvd(String id, String title) {
        super(id, title);
    }

    @Override
    public int getLoanPeriodDays() {
        return 7;
    }
}
