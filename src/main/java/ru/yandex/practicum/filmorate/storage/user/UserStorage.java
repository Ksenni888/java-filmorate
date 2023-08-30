package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

public interface UserStorage {

    public List<User> findAllUsers();

    public User createUser(User user);

    public User updateUser(User user);

}
