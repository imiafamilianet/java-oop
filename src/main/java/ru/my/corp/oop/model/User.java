package ru.my.corp.oop.model;

import lombok.AccessLevel;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;

    @Getter(AccessLevel.NONE)
    private final String password;

    private Wallet wallet;

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

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public void setWallet(Wallet wallet) {
        if (wallet != null) {
            this.wallet = wallet;
        }
    }
}
