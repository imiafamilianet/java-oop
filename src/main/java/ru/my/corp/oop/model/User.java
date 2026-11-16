package ru.my.corp.oop.model;

import lombok.AccessLevel;
import lombok.Getter;
import java.io.Serializable;

/**
 * Модель пользователя системы управления личными финансами.
 */
@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Уникальный логин пользователя.
     */
    private final String login;

    /**
     * Пароль пользователя для авторизации.
     */
    @Getter(AccessLevel.NONE)
    private final String password;

    /**
     * Персональный кошелек, привязанный к пользователю.
     */
    private Wallet wallet;

    /**
     * Создание нового пользователя с инициализацией пустого кошелька.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @throws IllegalArgumentException если логин или пароль пустые
     */
    public User(String login, String password) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым.");
        }
        this.login = login.trim();
        this.password = password;
        this.wallet = new Wallet();
    }

    /**
     * Проверка совпадения введенного пароля с паролем пользователя.
     *
     * @param inputPassword проверяемый пароль
     * @return true, если пароль верный, иначе false
     */
    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * Установка или обновление кошелька пользователя.
     *
     * @param wallet новый объект кошелька (игнорируется, если передан null)
     */
    public void setWallet(Wallet wallet) {
        if (wallet != null) {
            this.wallet = wallet;
        }
    }
}
