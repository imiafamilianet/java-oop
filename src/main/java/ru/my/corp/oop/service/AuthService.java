package ru.my.corp.oop.service;

import ru.my.corp.oop.model.User;

/**
 * Сервис регистрации и авторизации пользователей.
 */
public interface AuthService {

    /**
     * Регистрация нового пользователя в системе.
     *
     * @param login    логин нового пользователя
     * @param password пароль нового пользователя
     * @return созданный и авторизованный пользователь
     */
    User register(String login, String password);

    /**
     * Авторизация пользователя по логину и паролю.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return авторизованный пользователь с загруженным кошельком
     */
    User login(String login, String password);

    /**
     * Выход текущего пользователя из системы с сохранением данных кошелька.
     */
    void logout();

    /**
     * Получение текущего авторизованного пользователя.
     *
     * @return текущий пользователь или null, если никто не авторизован
     */
    User getCurrentUser();

    /**
     * Проверка, авторизован ли пользователь в текущей сессии.
     *
     * @return true, если пользователь вошел в систему, иначе false
     */
    boolean isAuthenticated();
}