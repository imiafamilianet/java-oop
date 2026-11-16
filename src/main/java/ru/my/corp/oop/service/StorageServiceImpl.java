package ru.my.corp.oop.service;

import ru.my.corp.oop.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Реализация {@link StorageService}.
 */
public class StorageServiceImpl implements StorageService {
    private static final String DATA_DIR = "data";

    /**
     * Конструктор.
     */
    public StorageServiceImpl() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для хранения данных: " + e.getMessage());
        }
    }

    /**
     * Сохранение данных пользователя и его кошелька в файл.
     *
     * @param user данные пользователя для сохранения
     */
    public void saveUser(User user) {
        if (user == null) return;
        Path filePath = getUserFilePath(user.getLogin());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения данных пользователя: " + e.getMessage());
        }
    }

    /**
     * Загрузка данных пользователя по логину из файла.
     *
     * @param login логин пользователя
     * @return Optional с объектом пользователя, если файл найден, иначе пустой Optional
     */
    public Optional<User> loadUser(String login) {
        if (login == null || login.trim().isEmpty()) {
            return Optional.empty();
        }
        Path filePath = getUserFilePath(login.trim());
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            User user = (User) ois.readObject();
            return Optional.of(user);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка загрузки данных пользователя: " + e.getMessage());
        }
    }

    /**
     * Проверка существования сохраненного пользователя с указанным логином.
     *
     * @param login логин пользователя
     * @return true, если данные пользователя существуют, иначе false
     */
    public boolean userExists(String login) {
        if (login == null || login.trim().isEmpty()) return false;
        return Files.exists(getUserFilePath(login.trim()));
    }

    private Path getUserFilePath(String login) {
        // Очищаем имя файла от недопустимых символов
        String safeLogin = login.toLowerCase().replaceAll("[^a-zA-Z0-9а-яА-Я_-]", "_");
        return Paths.get(DATA_DIR, safeLogin + ".dat");
    }
}
