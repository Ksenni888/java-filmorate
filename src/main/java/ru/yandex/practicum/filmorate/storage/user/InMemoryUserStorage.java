package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
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

    public User createUser(User user) {
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("The user has been added to the database {}", user);
        return user;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("User information has been updated {}", user);
        return user;
    }

    public User findById(Integer id) {
        return users.get(id);
    }

    public boolean containsUser(Integer id) {
        return users.containsKey(id);
    }

    private int generateUserId() {
        return ++unUserId;
    }
}
