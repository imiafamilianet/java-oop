package ru.my.corp.oop.model;

import java.io.Serial;

/**
 * Модель финансовой операции расхода.
 */
public class Expense extends Operation {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Создание операции расхода без комментария.
     *
     * @param amount   сумма расхода
     * @param category категория расхода
     */
    public Expense(double amount, Category category) {
        super(amount, category, "");
    }

    /**
     * Создание операции расхода с комментарием.
     *
     * @param amount      сумма расхода
     * @param category    категория расхода
     * @param description комментарий к расходу
     */
    public Expense(double amount, Category category, String description) {
        super(amount, category, description);
    }

    /**
     * Получение суммы расхода со знаком минус для вычитания из баланса кошелька.
     *
     * @return отрицательное значение суммы расхода
     */
    @Override
    public double getSignedAmount() {
        return -getAmount();
    }

    /**
     * Получение типа финансовой операции.
     *
     * @return тип операции {@link OperationType#EXPENSE}
     */
    @Override
    public OperationType getType() {
        return OperationType.EXPENSE;
    }
}
