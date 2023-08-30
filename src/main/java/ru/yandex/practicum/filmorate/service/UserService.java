package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeption.NoInformationFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {
    private final UserStorage userStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = userStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;

    }

    public User getUserByIdService(Integer id) {
        for (User u : userStorage.findAllUsers()) {
            if (u.getId() == id) {
                log.info("Information about user id=" + id + ":" + u);
                return u;
            }
        }
        log.info(String.format("User c id=%d not exist", id));
        throw new ObjectNotFoundException(String.format("User c id=%d not exist", id));

    }

    public List<User> addFriendsService(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            log.warn("User's and friend's id must be over 0");
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            log.warn(String.format("User c id=%d not exist", id));
            throw new NoInformationFoundException(String.format("User c id=%d not exist", id));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(friendId)) {
            log.warn(String.format("User c id=%d not exist", friendId));
            throw new NoInformationFoundException(String.format("User c id=%d not exist", friendId));

        }

        inMemoryUserStorage.getUsers().get(id).getFriends().add(friendId);
        inMemoryUserStorage.getUsers().get(friendId).getFriends().add(id);

        List<User> userAddFriend = new ArrayList<>();
        for (User u : inMemoryUserStorage.getUsers().values()) {
            if (u.getId() == friendId) {
                userAddFriend.add(u);
            }
        }
        log.info("Friend added");
        return userAddFriend;
    }

    public void deleteFriendsByIdService(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            log.warn(String.format("User with id=%d not found", id));
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(friendId)) {
            log.warn(String.format("User with id=%d not found", friendId));
            throw new NoInformationFoundException(String.format("User with id=%d not found", friendId));
        }
        inMemoryUserStorage.getUsers().get(id).getFriends().remove(friendId);
        inMemoryUserStorage.getUsers().get(friendId).getFriends().remove(id);
    }

    public List<User> getListOfFriendsService(Integer id) {
        if (id <= 0) {
            log.warn("User's and friend's id must be over 0");
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (inMemoryUserStorage.getUsers().isEmpty()) {
            log.warn("No users in base");
            return null;
        }
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            log.warn(String.format("User with id=%d not found", id));
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }

        List<User> userFriendsList = new ArrayList<>();
        for (Integer i : inMemoryUserStorage.getUsers().get(id).getFriends()) {
            if (inMemoryUserStorage.getUsers().containsKey(i)) {
                userFriendsList.add(inMemoryUserStorage.getUsers().get(i));
            }
        }
        log.info("List of friends done");
        return userFriendsList;
    }

    public List<User> commonFriends(Integer id, Integer otherId) {
        if (id <= 0) {
            log.warn("User's and friend's id must be over 0");
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            log.warn(String.format("User with id=%d not found", id));
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(otherId)) {
            log.warn(String.format("User with id=%d not found", otherId));
            throw new NoInformationFoundException(String.format("User with id=%d not found", otherId));
        }
        List<Integer> listNumberCommonFriends = new ArrayList<>();
        for (Integer friend : inMemoryUserStorage.getUsers().get(id).getFriends()) {
            if (inMemoryUserStorage.getUsers().get(otherId).getFriends().contains(friend)) {
                listNumberCommonFriends.add(friend);
            }
        }
        List<User> listCommonFriends = new ArrayList<>();
        for (Integer f : listNumberCommonFriends) {
            if (inMemoryUserStorage.getUsers().containsKey(f)) {
                listCommonFriends.add(inMemoryUserStorage.getUsers().get(f));
            }
        }
        log.info("List of common friends done");
        return listCommonFriends;
    }

}
