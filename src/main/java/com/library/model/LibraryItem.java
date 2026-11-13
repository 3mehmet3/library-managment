package com.library.model;

public abstract class LibraryItem {

    private final String id;
    private final String title;

    protected LibraryItem(String id, String title) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id must not be blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Item title must not be blank");
        }
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public abstract int getLoanPeriodDays();
}
