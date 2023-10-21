package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpa() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Mpa mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }

    @Override
    public Mpa getMpaById(Integer id) {
        if (!containsMpa(id)) {
            throw new ObjectNotFoundException(String.format("Mpa with id=%d not found", id));
        }
        return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE mpa_id=?",
                (resultSet, rowNum) -> {
                    Mpa newMpa = new Mpa();
                    newMpa.setId(id);
                    newMpa.setName(resultSet.getString("mpa_name"));
                    return newMpa;
                }, id);
    }

    @Override
    public boolean containsMpa(Integer id) {
        try {
            Long count = jdbcTemplate.queryForObject("select count(mpa_id) from mpa where mpa_id = ?", Long.class, id);
            return count == 1;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(String.format("Mpa with id=%d not found", id));
        }
    }
}