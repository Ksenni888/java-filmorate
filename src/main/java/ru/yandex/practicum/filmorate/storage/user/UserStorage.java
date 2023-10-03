package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public List<User> findAllUsers();

    public User createUser(User user);

    public User updateUser(User user);

    public User findById(Integer id);

    public boolean containsUser(Integer id);

    public void addFriends(Integer id, Integer friendId);

    public void deleteFriendsById(Integer id, Integer friendId);

    public List<User> getFriends(Integer id);

    public List<User> commonFriends(Integer id, Integer otherId);

}
