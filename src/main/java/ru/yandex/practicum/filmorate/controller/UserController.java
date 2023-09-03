package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserStorage userStorage;

    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Get information about user by id=" + id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public List<User> getListOfFriends(@PathVariable Integer id) {
        log.info("List of friends user id=" + id);
        return userService.getListOfFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public List<User> commonFriends(@PathVariable Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int otherId = Integer.parseInt(pathVarsMap.get("otherId"));
        log.info("Common friends");
        return userService.commonFriends(id, otherId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Create user");
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Update user");
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public List<User> addFriends(@PathVariable Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int friendId = Integer.parseInt(pathVarsMap.get("friendId"));
        log.info("Add friend");
        return userService.addFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public void deleteFriendsById(@PathVariable Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int friendId = Integer.parseInt(pathVarsMap.get("friendId"));
        log.info("Delete friends by id");
        userService.deleteFriendsById(id, friendId);
    }
}