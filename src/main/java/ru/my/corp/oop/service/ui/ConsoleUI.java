package ru.my.corp.oop.service.ui;

import lombok.RequiredArgsConstructor;
import ru.my.corp.oop.model.OperationType;
import ru.my.corp.oop.model.User;
import ru.my.corp.oop.service.FinanceService;
import ru.my.corp.oop.service.AuthService;

import java.util.*;

/**
 * Консольный интерфейс.
 */
@RequiredArgsConstructor
public class ConsoleUI {
    private final AuthService authService;
    private final FinanceService financeService;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Точка входа.
     */
    public void start() {
        System.out.println("=== Система управления личными финансами ===");
        boolean running = true;

        while (running) {
            if (!authService.isAuthenticated()) {
                running = showAuthMenu();
            } else {
                showMainMenu();
            }
        }
        System.out.println("Работа приложения завершена. До свидания!");
    }

    private boolean showAuthMenu() {
        System.out.println("\n--- Меню авторизации ---");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("0. Выйти из приложения");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "0":
                    return false;
                default:
                    System.out.println("Ошибка: неизвестная команда. Введите 1, 2 или 0.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return true;
    }

    private void handleLogin() {
        System.out.print("Введите логин: ");
        String login = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        User user = authService.login(login, password);
        System.out.println("Добро пожаловать, " + user.getLogin() + "! Данные кошелька загружены.");
    }

    private void handleRegister() {
        System.out.print("Придумайте логин: ");
        String login = scanner.nextLine();
        System.out.print("Придумайте пароль: ");
        String password = scanner.nextLine();

        User user = authService.register(login, password);
        System.out.println("Аккаунт '" + user.getLogin() + "' успешно создан!");
    }

    private void showMainMenu() {
        User user = authService.getCurrentUser();
        System.out.printf(Locale.US, "%n--- Личный кабинет [%s] | Текущий баланс: %,.2f ---%n",
                user.getLogin(), user.getWallet().getBalance());
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Создать категорию");
        System.out.println("4. Установить бюджет на категорию расходов");
        System.out.println("5. Показать общую статистику и бюджеты (в терминал)");
        System.out.println("6. Сохранить статистику в файл");
        System.out.println("7. Подсчитать сумму по выбранным категориям");
        System.out.println("8. Перевести средства другому пользователю");
        System.out.println("9. Показать историю всех операций");
        System.out.println("0. Выйти из аккаунта (с сохранением данных)");
        System.out.print("Выберите команду: ");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    handleAddIncome(user);
                    break;
                case "2":
                    handleAddExpense(user);
                    break;
                case "3":
                    handleCreateCategory(user);
                    break;
                case "4":
                    handleSetBudget(user);
                    break;
                case "5":
                    System.out.println("\n" + financeService.generateFullReport(user));
                    break;
                case "6":
                    handleExportReport(user);
                    break;
                case "7":
                    handleSelectedCategoriesStats(user);
                    break;
                case "8":
                    handleTransfer(user);
                    break;
                case "9":
                    handleShowHistory(user);
                    break;
                case "0":
                    authService.logout();
                    System.out.println("Данные кошелька успешно сохранены. Вы вышли из аккаунта.");
                    break;
                default:
                    System.out.println("Ошибка: неизвестная команда. Выберите пункт от 0 до 9.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleAddIncome(User user) {
        System.out.print("Введите категорию дохода (например, Зарплата, Бонус): ");
        String category = readNonEmptyString();
        double amount = readPositiveDouble("Введите сумму дохода: ");
        System.out.print("Комментарий (необязательно): ");
        String desc = scanner.nextLine();

        financeService.addIncome(user, category, amount, desc);
        System.out.println("Доход успешно добавлен.");
    }

    private void handleAddExpense(User user) {
        System.out.print("Введите категорию расхода (например, Еда, Такси): ");
        String category = readNonEmptyString();
        double amount = readPositiveDouble("Введите сумму расхода: ");
        System.out.print("Комментарий (необязательно): ");
        String desc = scanner.nextLine();

        List<String> alerts = financeService.addExpense(user, category, amount, desc);
        System.out.println("Расход успешно добавлен.");
        alerts.forEach(System.out::println);
    }

    private void handleCreateCategory(User user) {
        System.out.print("Введите название новой категории: ");
        String name = readNonEmptyString();
        OperationType type = readOperationType();

        financeService.createCategory(user, name, type);
        System.out.println("Категория '" + name + "' (" + type.getTitle() + ") создана.");
    }

    private void handleSetBudget(User user) {
        System.out.print("Введите категорию расходов для установки бюджета: ");
        String category = readNonEmptyString();
        double limit = readNonNegativeDouble("Введите лимит бюджета: ");

        financeService.setBudget(user, category, limit);
        System.out.println("Бюджет для категории '" + category + "' установлен: " + limit);
    }

    private void handleExportReport(User user) {
        System.out.print("Введите имя файла для сохранения (например, report.txt): ");
        String fileName = readNonEmptyString();
        financeService.exportReportToFile(user, fileName);
        System.out.println("Отчет успешно сохранен в файл: " + fileName);
    }

    private void handleSelectedCategoriesStats(User user) {
        OperationType type = readOperationType();
        System.out.print("Введите названия категорий через запятую (например, Еда, Такси): ");
        String line = readNonEmptyString();
        List<String> names = Arrays.asList(line.split(","));

        List<String> warnings = new ArrayList<>();
        Map<String, Double> stats = financeService.calculateSelectedCategories(
                user.getWallet(), names, type, warnings);

        warnings.forEach(w -> System.out.println("Уведомление: " + w));

        if (!stats.isEmpty()) {
            double total = 0;
            System.out.println("\nРезультаты подсчета:");
            for (Map.Entry<String, Double> entry : stats.entrySet()) {
                System.out.printf(Locale.US, "%s: %,.1f%n", entry.getKey(), entry.getValue());
                total += entry.getValue();
            }
            System.out.printf(Locale.US, "Итого по выбранным категориям: %,.1f%n", total);
        }
    }

    private void handleTransfer(User user) {
        System.out.print("Введите логин получателя: ");
        String recipient = readNonEmptyString();
        double amount = readPositiveDouble("Введите сумму перевода: ");
        System.out.print("Комментарий к переводу (необязательно): ");
        String comment = scanner.nextLine();

        List<String> alerts = financeService.transfer(user, recipient, amount, comment);
        System.out.println("Перевод пользователю '" + recipient + "' на сумму " + amount + " выполнен.");
        alerts.forEach(System.out::println);
    }

    private void handleShowHistory(User user) {
        var operations = user.getWallet().getOperations();
        if (operations.isEmpty()) {
            System.out.println("История операций пуста.");
            return;
        }
        System.out.println("\n--- История операций ---");
        operations.forEach(System.out::println);
    }

    // --- Вспомогательные методы валидации ввода ---

    private String readNonEmptyString() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print("Поле не может быть пустым. Повторите ввод: ");
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(",", ".");
            try {
                double val = Double.parseDouble(input);
                if (val > 0) {
                    return val;
                }
                System.out.println("Ошибка: число должно быть больше нуля.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            }
        }
    }

    private double readNonNegativeDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(",", ".");
            try {
                double val = Double.parseDouble(input);
                if (val >= 0) {
                    return val;
                }
                System.out.println("Ошибка: число не может быть отрицательным.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            }
        }
    }

    private OperationType readOperationType() {
        while (true) {
            System.out.print("Выберите тип (1 - Доход, 2 - Расход): ");
            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) return OperationType.INCOME;
            if ("2".equals(choice)) return OperationType.EXPENSE;
            System.out.println("Ошибка: введите 1 или 2.");
        }
    }
}
