package ru.my.corp.oop;
import ru.my.corp.oop.service.*;
import ru.my.corp.oop.service.ui.ConsoleUI;

/**
 * Точка входа в приложение.
 */
public class Main {
    public static void main(String[] args) {
        var storageService = new StorageServiceImpl();
        var authService = new AuthServiceImpl(storageService);
        var financeService = new FinanceServiceImpl(storageService);

        var consoleUI = new ConsoleUI(authService, financeService);
        consoleUI.start();
    }
}