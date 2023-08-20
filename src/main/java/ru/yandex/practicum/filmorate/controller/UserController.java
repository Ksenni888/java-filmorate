package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    HashMap<Integer, User> users = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int unUserId;

    @GetMapping
    public List<User> findAllUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getId() != 0) {
            log.warn("id должен быть пустым");
            throw new ValidationException("id должен быть пустым");

        } else if (user.getLogin().contains(" ")) {
            log.warn("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        } else {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(generateUserId());
            users.put(user.getId(), user);
            log.info("Пользователь добавлен в базу {}", user);
            return user;
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Такого пользователя нет в базе");
            throw new ValidationException("Такого пользователя нет в базе");

        } else if (user.getLogin().contains(" ")) {
            log.warn("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");

        } else {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Информация о пользователе обновлена {}", user);
            return user;
        }
    }

    private int generateUserId() {
        return ++unUserId;
    }
}