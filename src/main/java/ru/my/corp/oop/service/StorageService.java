package ru.my.corp.oop.service;

import ru.my.corp.oop.model.User;

import java.util.Optional;

/**
 * Сервис работы с данными пользователя.
 */
public interface StorageService {
    /**
     * Сохранение данных пользователя и его кошелька в файл.
     *
     * @param user данные пользователя для сохранения
     */
    void saveUser(User user);

    /**
     * Загрузка данных пользователя по логину из файла.
     *
     * @param login логин пользователя
     * @return Optional с объектом пользователя, если файл найден, иначе пустой Optional
     */
    Optional<User> loadUser(String login);

    /**
     * Проверка существования сохраненного пользователя с указанным логином.
     *
     * @param login логин пользователя
     * @return true, если данные пользователя существуют, иначе false
     */
    boolean userExists(String login);
}
