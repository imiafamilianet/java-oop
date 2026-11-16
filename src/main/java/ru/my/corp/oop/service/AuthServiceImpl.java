package ru.my.corp.oop.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.my.corp.oop.model.User;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final StorageService storageService;

    @Getter
    private User currentUser;

    public User register(String login, String password) {
        if (storageService.userExists(login)) {
            throw new IllegalArgumentException("Пользователь с логином '" + login + "' уже существует.");
        }
        User newUser = new User(login, password);
        storageService.saveUser(newUser);
        this.currentUser = newUser;
        return newUser;
    }

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

    public void logout() {
        if (currentUser != null) {
            storageService.saveUser(currentUser);
            currentUser = null;
        }
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
