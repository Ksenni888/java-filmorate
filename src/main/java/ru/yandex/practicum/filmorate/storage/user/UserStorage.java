package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    public List<User> findAllUsers();

    public User createUser(User user);

    public User updateUser(User user);

    public Map<Integer, User> getAllUsers();

    public User findById(Integer id);

    public boolean containsUser(Integer id);
}
