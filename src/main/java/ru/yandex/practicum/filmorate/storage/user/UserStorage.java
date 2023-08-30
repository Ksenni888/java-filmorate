package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public List<User> findAllUsers();

    public User createUser(User user);

    public User updateUser(User user);

}
