package ru.my.corp.oop.model;

import java.io.Serial;

/**
 * Модель финансовой операции дохода.
 */
public class Income extends Operation {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Создание операции дохода без комментария.
     *
     * @param amount   сумма дохода
     * @param category категория дохода
     */
    public Income(double amount, Category category) {
        super(amount, category, "");
    }

    /**
     * Создание операции дохода с комментарием.
     *
     * @param amount      сумма дохода
     * @param category    категория дохода
     * @param description комментарий к доходу
     */
    public Income(double amount, Category category, String description) {
        super(amount, category, description);
    }

    /**
     * Получение суммы дохода со знаком плюс для прибавления к балансу кошелька.
     *
     * @return положительное значение суммы дохода
     */
    @Override
    public double getSignedAmount() {
        return getAmount();
    }

    /**
     * Получение типа финансовой операции.
     *
     * @return тип операции {@link OperationType#INCOME}
     */
    @Override
    public OperationType getType() {
        return OperationType.INCOME;
    }
}
