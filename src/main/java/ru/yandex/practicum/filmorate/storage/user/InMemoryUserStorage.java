package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class InMemoryUserStorage implements UserStorage {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int unUserId;

    public List<User> findAllUsers() {
        return List.copyOf(users.values());
    }

    public Map<Integer, User> findAllUsersHashMap() {
        return getUsers();
    }

    public User createUser(User user) {
        if (user.getId() != 0) {
            throw new ValidationException("ID must be empty");
        }
        if (user.getLogin().contains(" ")) {
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

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException("There is no such user in the database");
        }
        if (user.getLogin().contains(" ")) {
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
