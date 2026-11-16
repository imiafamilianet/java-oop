package ru.my.corp.oop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип направленности денежной операции.
 */
@Getter
@RequiredArgsConstructor
public enum OperationType {
    /**
     * Поступление денежных средств (доход).
     */
    INCOME("ДОХОД"),

    /**
     * Списание денежных средств (расход).
     */
    EXPENSE("РАСХОД");

    /**
     * Отображаемое наименование типа операции.
     */
    private final String title;
}
