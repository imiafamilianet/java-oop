package ru.my.corp.oop.model;

import java.io.Serial;

public class Expense extends Operation {
    @Serial
    private static final long serialVersionUID = 1L;

    public Expense(double amount, Category category) {
        super(amount, category, "");
    }

    public Expense(double amount, Category category, String description) {
        super(amount, category, description);
    }

    @Override
    public double getSignedAmount() {
        return -getAmount();
    }

    @Override
    public OperationType getType() {
        return OperationType.EXPENSE;
    }
}
