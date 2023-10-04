package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public Genre getGenreById(Integer id) {
        if (!containsGenre(id)) {
            throw new ObjectNotFoundException(String.format("Genre with id=%d not found", id));
        }
        return jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id=?",
                (resultSet, rowNum) -> {
                    Genre newGenre = new Genre();
                    newGenre.setId(id);
                    newGenre.setName(resultSet.getString("genre_name"));
                    return newGenre;
                }, id);
    }

    public boolean containsGenre(Integer id) {
        try {
            Long count = jdbcTemplate.queryForObject("select count(genre_id) from genre where genre_id = ?", Long.class, id);
            return count == 1;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(String.format("Genre with id=%d not found", id));
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}