package ru.my.corp.oop.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.my.corp.oop.model.User;

import java.util.Optional;

/**
 * Реализация {@link AuthService}.
 */
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final StorageService storageService;

    @Getter
    private User currentUser;

    /**
     * Регистрация нового пользователя в системе.
     *
     * @param login    логин нового пользователя
     * @param password пароль нового пользователя
     * @return созданный и авторизованный пользователь
     */
    public User register(String login, String password) {
        if (storageService.userExists(login)) {
            throw new IllegalArgumentException("Пользователь с логином '" + login + "' уже существует.");
        }
        User newUser = new User(login, password);
        storageService.saveUser(newUser);
        this.currentUser = newUser;
        return newUser;
    }

    /**
     * Авторизация пользователя по логину и паролю.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return авторизованный пользователь с загруженным кошельком
     */
    public User login(String login, String password) {
        Optional<User> userOpt = storageService.loadUser(login);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с логином '" + login + "' не найден.");
        }
        User user = userOpt.get();
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("Неверный пароль.");
        }
        this.currentUser = user;
        return user;
    }

    /**
     * Выход текущего пользователя из системы с сохранением данных кошелька.
     */
    public void logout() {
        if (currentUser != null) {
            storageService.saveUser(currentUser);
            currentUser = null;
        }
    }

    /**
     * Проверка, авторизован ли пользователь в текущей сессии.
     *
     * @return true, если пользователь вошел в систему, иначе false
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
