package ru.my.corp.oop.service;

import ru.my.corp.oop.model.Category;
import ru.my.corp.oop.model.OperationType;
import ru.my.corp.oop.model.User;
import ru.my.corp.oop.model.Wallet;

import java.util.List;
import java.util.Map;

/**
 * Сервис управления финансами.
 */
public interface FinanceService {

    /**
     * Создание пользовательской категории.
     *
     * @param user данные пользователя
     * @param name название новой категории
     * @param type тип категории (доход или расход)
     */
    void createCategory(User user, String name, OperationType type);

    /**
     * Добавление дохода.
     *
     * @param user         данные пользователя
     * @param categoryName название категории дохода
     * @param amount       сумма дохода
     * @param description  комментарий к операции
     */
    void addIncome(User user, String categoryName, double amount, String description);

    /**
     * Добавление расхода.
     *
     * @param user         данные пользователя
     * @param categoryName название категории расхода
     * @param amount       сумма расхода
     * @param description  комментарий к операции
     * @return список текстовых оповещений при превышении лимита бюджета или общих доходов
     */
    List<String> addExpense(User user, String categoryName, double amount, String description);

    /**
     * Установка бюджета на категорию расходов.
     *
     * @param user         данные пользователя
     * @param categoryName название категории расходов
     * @param limit        лимит бюджета
     */
    void setBudget(User user, String categoryName, double limit);

    /**
     * Перевод средств другому пользователю по логину.
     *
     * @param sender         пользователь-отправитель
     * @param recipientLogin логин пользователя-получателя
     * @param amount         сумма перевода
     * @param comment        комментарий к переводу
     * @return список текстовых оповещений отправителя при превышении лимитов
     */
    List<String> transfer(User sender, String recipientLogin, double amount, String comment);

    /**
     * Подсчет общей суммы операций по заданному типу.
     *
     * @param wallet кошелек пользователя
     * @param type   тип операции (доход или расход)
     * @return общая сумма операций выбранного типа
     */
    double getTotalByType(Wallet wallet, OperationType type);

    /**
     * Подсчет суммы операций по конкретной категории.
     *
     * @param wallet   кошелек пользователя
     * @param category категория операций
     * @return общая сумма по указанной категории
     */
    double getTotalByCategory(Wallet wallet, Category category);

    /**
     * Подсчет сумм по нескольким выбранным категориям с проверкой их существования.
     *
     * @param wallet           кошелек пользователя
     * @param categoryNames    список названий категорий для подсчета
     * @param type             тип операций (доход или расход)
     * @param notFoundWarnings список для записи предупреждений о ненайденных категориях
     * @return карта с названиями найденных категорий и суммами по ним
     */
    Map<String, Double> calculateSelectedCategories(
            Wallet wallet,
            List<String> categoryNames,
            OperationType type,
            List<String> notFoundWarnings
    );

    /**
     * Формирование полного финансового отчета по доходам, расходам и бюджетам.
     *
     * @param user данные пользователя
     * @return сформированный текстовый отчет
     */
    String generateFullReport(User user);

    /**
     * Сохранение полного финансового отчета в текстовый файл.
     *
     * @param user     данные пользователя
     * @param filePath путь и имя файла для сохранения отчета
     */
    void exportReportToFile(User user, String filePath);
}
