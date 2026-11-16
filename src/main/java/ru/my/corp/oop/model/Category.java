package ru.my.corp.oop.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Модель категории финансовых операций (доходов или расходов).
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Нормализованное название категории (в нижнем регистре) для проверки уникальности.
     */
    @EqualsAndHashCode.Include
    private final String normalizedName;

    /**
     * Отображаемое название категории.
     */
    private final String name;

    /**
     * Тип операций, к которому относится категория (доход или расход).
     */
    @EqualsAndHashCode.Include
    private final OperationType type;

    /**
     * Создание новой категории операций.
     *
     * @param name название категории
     * @param type тип категории (доход или расход)
     * @throws IllegalArgumentException если название пустое или не указан тип категории
     */
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

    /**
     * Формирование строкового представления категории с указанием ее типа.
     *
     * @return строка формата "Название (ТИП)"
     */
    @Override
    public String toString() {
        return name + " (" + type.getTitle() + ")";
    }
}
