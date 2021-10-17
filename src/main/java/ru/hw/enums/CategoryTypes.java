package ru.hw.enums;

import lombok.Getter;

public enum CategoryTypes {
    FOOD("Food", 1),
    ELECTRONICS("Electronics", 2),
    FURNITURE("Furniture", 3);

    @Getter
    private final String title;
    @Getter
    private final Integer id;

    CategoryTypes(String title, Integer id) {
        this.title = title;
        this.id = id;
    }
}
