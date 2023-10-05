package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAllUsers() {
        return jdbcTemplate.query(
                "SELECT * FROM users",
                (resultSet, rowNum) -> userParameters(resultSet));
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO users (email, login, user_name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
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
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE user_id=?",
                (resultSet, rowNum) -> userParameters(resultSet), id);
    }

    @Override
    public boolean containsUser(Integer id) {
        Long count = jdbcTemplate.queryForObject(
                "select count(user_id) from users where user_id = ?", Long.class, id);
        return count == 1;
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
        return this.jdbcTemplate.query(
                "SELECT * FROM users WHERE user_id in (SELECT friend_id FROM friendship WHERE user_id=?)",
                (resultSet, rowNum) -> userParameters(resultSet), id);
    }

    public List<User> commonFriends(Integer id, Integer otherId) {
        List<User> firstId = getFriends(id);
        List<User> secondOtherId = getFriends(otherId);
        firstId.retainAll(secondOtherId);
        return firstId;
    }

    public User userParameters(ResultSet resultSet) {
        try {
            User user = new User();
            user.setId(resultSet.getInt("user_id"));
            user.setEmail(resultSet.getString("email"));
            user.setLogin(resultSet.getString("login"));
            user.setName(resultSet.getString("user_name"));
            user.setBirthday(resultSet.getDate("birthday").toLocalDate());
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}