package ru.my.corp.oop.service;

import lombok.RequiredArgsConstructor;
import ru.my.corp.oop.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис управлления финансами.
 */
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {
    private final StorageService storageService;

    // 1. Создание пользовательской категории
    public void createCategory(User user, String name, OperationType type) {
        Wallet wallet = user.getWallet();
        Optional<Category> existing = wallet.findCategory(name, type);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Категория '" + name + "' (" + type.getTitle() + ") уже существует.");
        }
        Category category = new Category(name, type);
        wallet.addCategory(category);
    }

    // 2. Добавление дохода
    public void addIncome(User user, String categoryName, double amount, String description) {
        Wallet wallet = user.getWallet();
        // Если категории еще нет — создаем автоматически для удобства пользователя
        Category category = wallet.findCategory(categoryName, OperationType.INCOME)
                .orElseGet(() -> new Category(categoryName, OperationType.INCOME));

        wallet.addOperation(new Income(amount, category, description));
    }

    // 3. Добавление расхода с возвратом списка оповещений (если есть превышения)
    public List<String> addExpense(User user, String categoryName, double amount, String description) {
        Wallet wallet = user.getWallet();
        Category category = wallet.findCategory(categoryName, OperationType.EXPENSE)
                .orElseGet(() -> new Category(categoryName, OperationType.EXPENSE));

        wallet.addOperation(new Expense(amount, category, description));

        return checkAlerts(wallet, category);
    }

    // 4. Установка бюджета на категорию расходов
    public void setBudget(User user, String categoryName, double limit) {
        Wallet wallet = user.getWallet();
        Category category = wallet.findCategory(categoryName, OperationType.EXPENSE)
                .orElseGet(() -> new Category(categoryName, OperationType.EXPENSE));

        wallet.setBudget(category, limit);
    }

    // 5. Проверка оповещений по лимитам и общему балансу
    private List<String> checkAlerts(Wallet wallet, Category category) {
        List<String> alerts = new ArrayList<>();

        // Проверка превышения бюджета по конкретной категории
        if (wallet.hasBudget(category)) {
            double limit = wallet.getBudget(category);
            double spent = getTotalByCategory(wallet, category);
            if (spent > limit) {
                alerts.add(String.format("ВНИМАНИЕ! Превышен лимит бюджета по категории '%s' (Лимит: %.1f, Потрачено: %.1f, Перерасход: %.1f)",
                        category.getName(), limit, spent, spent - limit));
            }
        }

        // Проверка превышения общих расходов над общими доходами
        double totalIncome = getTotalByType(wallet, OperationType.INCOME);
        double totalExpense = getTotalByType(wallet, OperationType.EXPENSE);
        if (totalExpense > totalIncome) {
            alerts.add(String.format("ВНИМАНИЕ! Общие расходы (%.1f) превысили общие доходы (%.1f)!",
                    totalExpense, totalIncome));
        }

        return alerts;
    }

    // 6. Перевод средств другому пользователю по логину (Дополнительное задание)
    public List<String> transfer(User sender, String recipientLogin, double amount, String comment) {
        if (sender.getLogin().equalsIgnoreCase(recipientLogin.trim())) {
            throw new IllegalArgumentException("Нельзя перевести средства самому себе.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля.");
        }

        User recipient = storageService.loadUser(recipientLogin)
                .orElseThrow(() -> new IllegalArgumentException("Получатель с логином '" + recipientLogin + "' не найден."));

        // Фиксируем расход у отправителя
        String outDesc = "Перевод пользователю " + recipient.getLogin() + (comment.isEmpty() ? "" : ": " + comment);
        List<String> alerts = addExpense(sender, "Переводы (исходящие)", amount, outDesc);

        // Фиксируем доход у получателя и сразу сохраняем его кошелек на диск
        String inDesc = "Перевод от пользователя " + sender.getLogin() + (comment.isEmpty() ? "" : ": " + comment);
        addIncome(recipient, "Переводы (входящие)", amount, inDesc);
        storageService.saveUser(recipient);

        return alerts;
    }

    // 7. Подсчет общей суммы по типу (ДОХОД / РАСХОД)
    public double getTotalByType(Wallet wallet, OperationType type) {
        return wallet.getOperations().stream()
                .filter(op -> op.getType() == type)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    // 8. Подсчет суммы по конкретной категории
    public double getTotalByCategory(Wallet wallet, Category category) {
        return wallet.getOperations().stream()
                .filter(op -> op.getCategory().equals(category))
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    // 9. Подсчет по нескольким выбранным категориям с проверкой существования
    public Map<String, Double> calculateSelectedCategories(Wallet wallet, List<String> categoryNames, OperationType type, List<String> notFoundWarnings) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (String rawName : categoryNames) {
            String trimmed = rawName.trim();
            if (trimmed.isEmpty()) continue;

            Optional<Category> catOpt = wallet.findCategory(trimmed, type);
            if (catOpt.isEmpty()) {
                notFoundWarnings.add("Категория '" + trimmed + "' (" + type.getTitle() + ") не найдена.");
            } else {
                Category cat = catOpt.get();
                result.put(cat.getName(), getTotalByCategory(wallet, cat));
            }
        }
        return result;
    }

    // 10. Формирование полного финансового отчета (как в примере из ТЗ)
    public String generateFullReport(User user) {
        Wallet wallet = user.getWallet();
        StringBuilder sb = new StringBuilder();

        double totalIncome = getTotalByType(wallet, OperationType.INCOME);
        double totalExpense = getTotalByType(wallet, OperationType.EXPENSE);

        sb.append(String.format(Locale.US, "Общий доход: %,.1f%n", totalIncome));

        // Группировка доходов по категориям
        Map<String, Double> incomeByCat = wallet.getOperations().stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .collect(Collectors.groupingBy(
                        op -> op.getCategory().getName(),
                        LinkedHashMap::new,
                        Collectors.summingDouble(Operation::getAmount)
                ));

        if (!incomeByCat.isEmpty()) {
            sb.append("Доходы по категориям:\n");
            incomeByCat.forEach((catName, sum) ->
                    sb.append(String.format(Locale.US, "%s: %,.1f%n", catName, sum)));
        }

        sb.append(String.format(Locale.US, "Общие расходы: %,.1f%n", totalExpense));

        // Вывод бюджетов и остатков по категориям расходов
        Map<Category, Double> budgets = wallet.getCategoryBudgets();
        if (!budgets.isEmpty()) {
            sb.append("Бюджет по категориям:\n");
            budgets.forEach((category, limit) -> {
                double spent = getTotalByCategory(wallet, category);
                double remaining = limit - spent;
                sb.append(String.format(Locale.US, "%s: %.1f, Оставшийся бюджет: %.1f%n",
                        category.getName(), limit, remaining));
            });
        }

        return sb.toString();
    }

    // 11. Сохранение отчета в текстовый файл
    public void exportReportToFile(User user, String filePath) {
        String report = generateFullReport(user);
        try {
            Files.write(Paths.get(filePath), report.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить отчет в файл: " + e.getMessage());
        }
    }
}
