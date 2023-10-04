package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAllUsers() {
        List<User> userBack = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        while (userRows.next()) {
            User user = new User();
            userParameters(user, userRows);
            userBack.add(user);
        }
        return userBack;
    }

    @Override
    public User createUser(User user) {
        Integer idCount = jdbcTemplate.queryForObject("SELECT count(user_id) FROM users", Integer.class);
        if (idCount != null) {
            idCount = idCount + 1;
            user.setId(idCount);
        }
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, user_name, birthday) VALUES (?, ?, ?, ?, ?)",
                idCount, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email=?, login=?, user_name=?, birthday=? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User findById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id=?", id);
        User userBack = new User();
        if (userRows.next()) {
            userParameters(userBack, userRows);
            this.updateUser(userBack);
        }
        return userBack;
    }

    @Override
    public boolean containsUser(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id=?", id);
        return userRows.next();
    }

    public void addFriends(Integer id, Integer friendId) {
        jdbcTemplate.update(
                "INSERT INTO friendship (friend_id, user_id, status) VALUES (?, ?, ?)",
                friendId, id, true);
    }

    public void deleteFriendsById(Integer id, Integer friendId) {
        jdbcTemplate.update(
                "DELETE FROM friendship WHERE friend_id = ?",
                friendId);
    }

    public List<User> getFriends(Integer id) {
        List<User> friendsBack = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id in (SELECT friend_id FROM friendship WHERE user_id=?)", id);
        while (userRows.next()) {
            User user = new User();
            userParameters(user, userRows);
            friendsBack.add(user);
        }
        return friendsBack;
    }

    public List<User> commonFriends(Integer id, Integer otherId) {
        List<User> firstId = getFriends(id);
        List<User> secondOtherId = getFriends(otherId);
        firstId.retainAll(secondOtherId);
        return firstId;
    }

    public void userParameters(User user, SqlRowSet userRows) {
        user.setId(userRows.getInt("user_id"));
        user.setEmail(userRows.getString("email"));
        user.setLogin(userRows.getString("login"));
        user.setName(userRows.getString("user_name"));
        user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
    }
}