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

/**
 * Модель кошелька пользователя, хранящая баланс, категории, историю операций и бюджеты.
 */
public class Wallet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Текущий денежный баланс кошелька.
     */
    @Getter
    private double balance;

    /**
     * Набор всех доступных пользователю категорий доходов и расходов.
     */
    private final Set<Category> categories;

    /**
     * Хронологический список всех совершенных финансовых операций.
     */
    private final List<Operation> operations;

    /**
     * Карта установленных лимитов бюджета по категориям расходов.
     */
    private final Map<Category, Double> categoryBudgets;

    /**
     * Создание нового кошелька с нулевым балансом и базовым набором категорий.
     */
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

    /**
     * Добавление новой категории в кошелек.
     *
     * @param category категория операций для добавления
     * @throws IllegalArgumentException если переданная категория равна null
     */
    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Категория не может быть null.");
        }
        categories.add(category);
    }

    /**
     * Поиск категории в кошельке по названию и типу операции без учета регистра.
     *
     * @param name название искомой категории
     * @param type тип операции (доход или расход)
     * @return {@link Optional} с найденной категорией или пустой {@link Optional}, если категория не найдена
     */
    public Optional<Category> findCategory(String name, OperationType type) {
        if (name == null) return Optional.empty();
        String target = name.trim().toLowerCase();
        return categories.stream()
                .filter(c -> c.getNormalizedName().equals(target) && c.getType() == type)
                .findFirst();
    }

    /**
     * Добавление финансовой операции (дохода или расхода) в кошелек с пересчетом баланса.
     *
     * @param operation финансовая операция
     * @throws IllegalArgumentException если операция равна null
     */
    public void addOperation(Operation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Операция не может быть null.");
        }
        // Автоматически регистрируем категорию в кошельке, если она новая
        categories.add(operation.getCategory());
        operations.add(operation);
        balance += operation.getSignedAmount();
    }

    /**
     * Установка лимита бюджета для категории расходов.
     *
     * @param category категория расходов
     * @param limit    денежный лимит бюджета (не может быть отрицательным)
     * @throws IllegalArgumentException если категория равна null, не является категорией расходов или лимит отрицательный
     */
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

    /**
     * Получение установленного лимита бюджета по категории.
     *
     * @param category категория расходов
     * @return сумма лимита или null, если бюджет для этой категории не задан
     */
    public Double getBudget(Category category) {
        return categoryBudgets.get(category);
    }

    /**
     * Проверка наличия установленного лимита бюджета для указанной категории.
     *
     * @param category категория расходов
     * @return true, если лимит бюджета установлен, иначе false
     */
    public boolean hasBudget(Category category) {
        return categoryBudgets.containsKey(category);
    }

    /**
     * Получение неизменяемого набора всех категорий кошелька.
     *
     * @return неизменяемое множество категорий {@link Set}
     */
    public Set<Category> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    /**
     * Получение неизменяемого списка всех финансовых операций кошелька.
     *
     * @return неизменяемый список операций {@link List}
     */
    public List<Operation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    /**
     * Получение неизменяемой карты установленных бюджетов по категориям.
     *
     * @return неизменяемая карта {@link Map} (категория — лимит)
     */
    public Map<Category, Double> getCategoryBudgets() {
        return Collections.unmodifiableMap(categoryBudgets);
    }
}
