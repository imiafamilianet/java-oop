package ru.my.corp.oop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип направленности денежной операции.
 */
@Getter
@RequiredArgsConstructor
public enum OperationType {
    INCOME("ДОХОД"),
    EXPENSE("РАСХОД");

    private final String title;
}
