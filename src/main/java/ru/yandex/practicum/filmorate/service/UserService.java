package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeption.NoInformationFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(Integer id) {
        for (User u : userStorage.findAllUsers()) {
            if (u.getId() == id) {
                log.info("Information about user id=" + id + ":" + u);
                return u;
            }
        }
        throw new ObjectNotFoundException(String.format("User c id=%d not exist", id));
    }

    public List<User> addFriends(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!userStorage.findAllUsersHashMap().containsKey(id)) {
            throw new NoInformationFoundException(String.format("User c id=%d not exist", id));
        }
        if (!userStorage.findAllUsersHashMap().containsKey(friendId)) {
            throw new NoInformationFoundException(String.format("User c id=%d not exist", friendId));
        }

        userStorage.findAllUsersHashMap().get(id).getFriendsId().add(friendId);
        userStorage.findAllUsersHashMap().get(friendId).getFriendsId().add(id);

        List<User> userAddFriend = new ArrayList<>();
        for (User u : userStorage.findAllUsersHashMap().values()) {
            if (u.getId() == friendId) {
                userAddFriend.add(u);
            }
        }
        log.info("Friend added");
        return userAddFriend;
    }

    public void deleteFriendsById(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!userStorage.findAllUsersHashMap().containsKey(id)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }
        if (!userStorage.findAllUsersHashMap().containsKey(friendId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", friendId));
        }
        userStorage.findAllUsersHashMap().get(id).getFriendsId().remove(friendId);
        userStorage.findAllUsersHashMap().get(friendId).getFriendsId().remove(id);
    }

    public List<User> getListOfFriends(Integer id) {
        if (id <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (userStorage.findAllUsersHashMap().isEmpty()) {
            return null;
        }
        if (!userStorage.findAllUsersHashMap().containsKey(id)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }

        List<User> userFriendsList = new ArrayList<>();
        for (Integer i : userStorage.findAllUsersHashMap().get(id).getFriendsId()) {
            if (userStorage.findAllUsersHashMap().containsKey(i)) {
                userFriendsList.add(userStorage.findAllUsersHashMap().get(i));
            }
        }
        log.info("List of friends done");
        return userFriendsList;
    }

    public List<User> commonFriends(Integer id, Integer otherId) {
        if (id <= 0) {
            throw new ObjectNotFoundException("User's and friend's id must be over 0");
        }
        if (!userStorage.findAllUsersHashMap().containsKey(id)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", id));
        }
        if (!userStorage.findAllUsersHashMap().containsKey(otherId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", otherId));
        }
        List<Integer> listNumberCommonFriends = new ArrayList<>();
        for (Integer friend : userStorage.findAllUsersHashMap().get(id).getFriendsId()) {
            if (userStorage.findAllUsersHashMap().get(otherId).getFriendsId().contains(friend)) {
                listNumberCommonFriends.add(friend);
            }
        }
        List<User> listCommonFriends = new ArrayList<>();
        for (Integer f : listNumberCommonFriends) {
            if (userStorage.findAllUsersHashMap().containsKey(f)) {
                listCommonFriends.add(userStorage.findAllUsersHashMap().get(f));
            }
        }
        log.info("List of common friends done");
        return listCommonFriends;
    }
}
