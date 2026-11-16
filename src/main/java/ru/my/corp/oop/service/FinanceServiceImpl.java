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
 * Реализация {@link FinanceService}.
 */
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {
    private final StorageService storageService;

    /**
     * Создание пользовательской категории.
     *
     * @param user данные пользователя
     * @param name название новой категории
     * @param type тип категории (доход или расход)
     */
    public void createCategory(User user, String name, OperationType type) {
        var wallet = user.getWallet();
        Optional<Category> existing = wallet.findCategory(name, type);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Категория '" + name + "' (" + type.getTitle() + ") уже существует.");
        }
        var category = new Category(name, type);
        wallet.addCategory(category);
    }

    /**
     * Добавление дохода.
     *
     * @param user         данные пользователя
     * @param categoryName название категории дохода
     * @param amount       сумма дохода
     * @param description  комментарий к операции
     */
    public void addIncome(User user, String categoryName, double amount, String description) {
        var wallet = user.getWallet();
        var category = wallet.findCategory(categoryName, OperationType.INCOME)
                .orElseGet(() -> new Category(categoryName, OperationType.INCOME));

        wallet.addOperation(new Income(amount, category, description));
    }

    /**
     * Добавление расхода.
     *
     * @param user         данные пользователя
     * @param categoryName название категории расхода
     * @param amount       сумма расхода
     * @param description  комментарий к операции
     * @return список текстовых оповещений при превышении лимита бюджета или общих доходов
     */
    public List<String> addExpense(User user, String categoryName, double amount, String description) {
        var wallet = user.getWallet();
        var category = wallet.findCategory(categoryName, OperationType.EXPENSE)
                .orElseGet(() -> new Category(categoryName, OperationType.EXPENSE));

        wallet.addOperation(new Expense(amount, category, description));

        return checkAlerts(wallet, category);
    }

    /**
     * Установка бюджета на категорию расходов.
     *
     * @param user         данные пользователя
     * @param categoryName название категории расходов
     * @param limit        лимит бюджета
     */
    public void setBudget(User user, String categoryName, double limit) {
        var wallet = user.getWallet();
        var category = wallet.findCategory(categoryName, OperationType.EXPENSE)
                .orElseGet(() -> new Category(categoryName, OperationType.EXPENSE));

        wallet.setBudget(category, limit);
    }

    /**
     * Перевод средств другому пользователю по логину.
     *
     * @param sender         пользователь-отправитель
     * @param recipientLogin логин пользователя-получателя
     * @param amount         сумма перевода
     * @param comment        комментарий к переводу
     * @return список текстовых оповещений отправителя при превышении лимитов
     */
    public List<String> transfer(User sender, String recipientLogin, double amount, String comment) {
        if (sender.getLogin().equalsIgnoreCase(recipientLogin.trim())) {
            throw new IllegalArgumentException("Нельзя перевести средства самому себе.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля.");
        }

        var recipient = storageService.loadUser(recipientLogin)
                .orElseThrow(() -> new IllegalArgumentException("Получатель с логином '" + recipientLogin + "' не найден."));

        String outDesc = "Перевод пользователю " + recipient.getLogin() + (comment.isEmpty() ? "" : ": " + comment);
        List<String> alerts = addExpense(sender, "Переводы (исходящие)", amount, outDesc);

        String inDesc = "Перевод от пользователя " + sender.getLogin() + (comment.isEmpty() ? "" : ": " + comment);
        addIncome(recipient, "Переводы (входящие)", amount, inDesc);
        storageService.saveUser(recipient);

        return alerts;
    }

    /**
     * Подсчет общей суммы операций по заданному типу.
     *
     * @param wallet кошелек пользователя
     * @param type   тип операции (доход или расход)
     * @return общая сумма операций выбранного типа
     */
    public double getTotalByType(Wallet wallet, OperationType type) {
        return wallet.getOperations().stream()
                .filter(op -> op.getType() == type)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    /**
     * Подсчет суммы операций по конкретной категории.
     *
     * @param wallet   кошелек пользователя
     * @param category категория операций
     * @return общая сумма по указанной категории
     */
    public double getTotalByCategory(Wallet wallet, Category category) {
        return wallet.getOperations().stream()
                .filter(op -> op.getCategory().equals(category))
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    /**
     * Подсчет сумм по нескольким выбранным категориям с проверкой их существования.
     *
     * @param wallet           кошелек пользователя
     * @param categoryNames    список названий категорий для подсчета
     * @param type             тип операций (доход или расход)
     * @param notFoundWarnings список для записи предупреждений о ненайденных категориях
     * @return карта с названиями найденных категорий и суммами по ним
     */
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

    /**
     * Формирование полного финансового отчета по доходам, расходам и бюджетам.
     *
     * @param user данные пользователя
     * @return сформированный текстовый отчет
     */
    public String generateFullReport(User user) {
        var wallet = user.getWallet();
        var sb = new StringBuilder();

        double totalIncome = getTotalByType(wallet, OperationType.INCOME);
        double totalExpense = getTotalByType(wallet, OperationType.EXPENSE);

        sb.append(String.format(Locale.US, "Общий доход: %,.1f%n", totalIncome));

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

    /**
     * Сохранение полного финансового отчета в текстовый файл.
     *
     * @param user     данные пользователя
     * @param filePath путь и имя файла для сохранения отчета
     */
    public void exportReportToFile(User user, String filePath) {
        String report = generateFullReport(user);
        try {
            Files.write(Paths.get(filePath), report.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить отчет в файл: " + e.getMessage());
        }
    }

    private List<String> checkAlerts(Wallet wallet, Category category) {
        List<String> alerts = new ArrayList<>();

        if (wallet.hasBudget(category)) {
            double limit = wallet.getBudget(category);
            double spent = getTotalByCategory(wallet, category);
            if (spent > limit) {
                alerts.add(String.format("ВНИМАНИЕ! Превышен лимит бюджета по категории '%s' (Лимит: %.1f, Потрачено: %.1f, Перерасход: %.1f)",
                        category.getName(), limit, spent, spent - limit));
            }
        }

        double totalIncome = getTotalByType(wallet, OperationType.INCOME);
        double totalExpense = getTotalByType(wallet, OperationType.EXPENSE);
        if (totalExpense > totalIncome) {
            alerts.add(String.format("ВНИМАНИЕ! Общие расходы (%.1f) превысили общие доходы (%.1f)!",
                    totalExpense, totalIncome));
        }

        return alerts;
    }
}
