package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int unUserId;

    @GetMapping
    public List<User> findAllUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getId() != 0) {
            log.warn("ID must be empty");
            throw new ValidationException("ID must be empty");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("The login cannot contain spaces");
            throw new ValidationException("The login cannot contain spaces");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("The user has been added to the database {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("There is no such user in the database");
            throw new ValidationException("There is no such user in the database");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("The login cannot contain spaces");
            throw new ValidationException("The login cannot contain spaces");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User information has been updated {}", user);
        return user;
    }

    private int generateUserId() {
        return ++unUserId;
    }
}