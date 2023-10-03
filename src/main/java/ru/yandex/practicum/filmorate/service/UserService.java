package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NoInformationFoundException;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Autowired
    public UserService( @Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
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
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.containsUser(user.getId())) {
           throw new ObjectNotFoundException("There is no such user in the database");
       }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("The login cannot contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User findById(Integer id) {
        if (!userStorage.containsUser(id)) {
            throw new ObjectNotFoundException(String.format("User c id=%d not exist", id));
        }
        return userStorage.findById(id);
    }

    public void addFriends(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!userStorage.containsUser(id)) {
            throw new NoInformationFoundException(String.format("User c id=%d not exist", id));
        }
        if (!userStorage.containsUser(friendId)) {
            throw new NoInformationFoundException(String.format("User c id=%d not exist", friendId));
        }
        userStorage.addFriends(id,friendId);
        log.info("Friend added");
    }

    public void deleteFriendsById(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!userStorage.containsUser(id)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }
        if (!userStorage.containsUser(friendId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", friendId));
        }
        userStorage.deleteFriendsById(id, friendId);
    }

    public List<User> getFriends(Integer id) {
        if (id <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (userStorage.findAllUsers().isEmpty()) {
            return null;
        }
        if (!userStorage.containsUser(id)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }
        log.info("List of friends done");
        return userStorage.getFriends(id);
    }

    public List<User> commonFriends(Integer id, Integer otherId) {
        if (id <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!userStorage.containsUser(id)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }
        if (!userStorage.containsUser(otherId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", otherId));
        }
        log.info("List of common friends done");
        return userStorage.commonFriends(id, otherId);
    }
}
