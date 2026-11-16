package ru.my.corp.oop.model;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public abstract class Operation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final double amount;
    private final Category category;
    private final LocalDateTime timestamp;
    private final String description;

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

    public abstract double getSignedAmount();
    public abstract OperationType getType();

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String descPart = description.isEmpty() ? "" : " (" + description + ")";
        return String.format("[%s] %s | Категория: %s | Сумма: %.2f%s",
                timestamp.format(formatter), getType().getTitle(), category.getName(), amount, descPart);
    }
}
