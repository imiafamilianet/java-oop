package ru.my.corp.oop.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    private final String normalizedName;

    private final String name;

    @EqualsAndHashCode.Include
    private final OperationType type;

    public Category(String name, OperationType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Тип категории (доход/расход) обязателен.");
        }
        this.name = name.trim();
        this.normalizedName = this.name.toLowerCase();
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " (" + type.getTitle() + ")";
    }
}
