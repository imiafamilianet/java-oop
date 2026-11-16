package ru.my.corp.oop.model;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Wallet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private double balance;
    private final Set<Category> categories;
    private final List<Operation> operations;
    private final Map<Category, Double> categoryBudgets;

    public Wallet() {
        this.balance = 0.0;
        this.categories = new LinkedHashSet<>();
        this.operations = new ArrayList<>();
        this.categoryBudgets = new HashMap<>();
        initDefaultCategories();
    }

    // Базовые константные категории при создании кошелька
    private void initDefaultCategories() {
        addCategory(new Category("Зарплата", OperationType.INCOME));
        addCategory(new Category("Бонус", OperationType.INCOME));
        addCategory(new Category("Переводы (входящие)", OperationType.INCOME));

        addCategory(new Category("Еда", OperationType.EXPENSE));
        addCategory(new Category("Коммунальные услуги", OperationType.EXPENSE));
        addCategory(new Category("Развлечения", OperationType.EXPENSE));
        addCategory(new Category("Такси", OperationType.EXPENSE));
        addCategory(new Category("Переводы (исходящие)", OperationType.EXPENSE));
    }

    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Категория не может быть null.");
        }
        categories.add(category);
    }

    public Optional<Category> findCategory(String name, OperationType type) {
        if (name == null) return Optional.empty();
        String target = name.trim().toLowerCase();
        return categories.stream()
                .filter(c -> c.getNormalizedName().equals(target) && c.getType() == type)
                .findFirst();
    }

    public void addOperation(Operation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Операция не может быть null.");
        }
        // Автоматически регистрируем категорию в кошельке, если она новая
        categories.add(operation.getCategory());
        operations.add(operation);
        balance += operation.getSignedAmount();
    }

    public void setBudget(Category category, double limit) {
        if (category == null) {
            throw new IllegalArgumentException("Категория не может быть null.");
        }
        if (category.getType() != OperationType.EXPENSE) {
            throw new IllegalArgumentException("Бюджет можно установить только на категорию расходов.");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("Лимит бюджета не может быть отрицательным.");
        }
        categories.add(category);
        categoryBudgets.put(category, limit);
    }

    public Double getBudget(Category category) {
        return categoryBudgets.get(category);
    }

    public boolean hasBudget(Category category) {
        return categoryBudgets.containsKey(category);
    }

    public Set<Category> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public List<Operation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    public Map<Category, Double> getCategoryBudgets() {
        return Collections.unmodifiableMap(categoryBudgets);
    }
}
