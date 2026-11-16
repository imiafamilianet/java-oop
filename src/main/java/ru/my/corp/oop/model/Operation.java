package ru.my.corp.oop.model;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Абстрактный базовый класс финансовой операции (дохода или расхода).
 */
@Getter
public abstract class Operation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор операции.
     */
    private final String id;

    /**
     * Сумма финансовой операции.
     */
    private final double amount;

    /**
     * Категория, к которой относится операция.
     */
    private final Category category;

    /**
     * Дата и время совершения операции.
     */
    private final LocalDateTime timestamp;

    /**
     * Комментарий или описание операции.
     */
    private final String description;

    /**
     * Создание финансовой операции с проверкой корректности входных данных.
     *
     * @param amount      сумма операции (должна быть больше нуля)
     * @param category    категория операции (тип должен совпадать с типом операции)
     * @param description комментарий к операции
     * @throws IllegalArgumentException если сумма не положительна, категория равна null или не совпадает по типу
     */
    public Operation(double amount, Category category, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма операции должна быть больше нуля.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Категория не может быть null.");
        }
        if (category.getType() != getType()) {
            throw new IllegalArgumentException("Тип категории не совпадает с типом операции.");
        }
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.category = category;
        this.timestamp = LocalDateTime.now();
        this.description = (description != null) ? description.trim() : "";
    }

    /**
     * Получение суммы операции со знаком для расчета изменения баланса кошелька.
     *
     * @return положительная сумма для дохода или отрицательная для расхода
     */
    public abstract double getSignedAmount();

    /**
     * Получение типа финансовой операции.
     *
     * @return тип операции {@link OperationType}
     */
    public abstract OperationType getType();

    /**
     * Формирование читаемого строкового представления операции с датой, типом, категорией и суммой.
     *
     * @return отформатированная строка с информацией об операции
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String descPart = description.isEmpty() ? "" : " (" + description + ")";
        return String.format("[%s] %s | Категория: %s | Сумма: %.2f%s",
                timestamp.format(formatter), getType().getTitle(), category.getName(), amount, descPart);
    }
}
