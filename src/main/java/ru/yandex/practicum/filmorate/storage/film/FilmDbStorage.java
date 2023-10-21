package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAllFilms() {
        return jdbcTemplate.query(
                "SELECT * FROM films",
                (resultSet, rowNum) -> filmParameters(resultSet));
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO films (film_name, description, releaseDate, duration, mpa) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres().stream().mapToInt(Genre::getId).toArray() != null) {
            insertGenresInTable(film);
            film.setGenres(findGenresFilm(film));
        }
        return film;
    }

    @Override
    public Film rewriteFilm(Film film) {
        String sqlQuery = "UPDATE films SET film_name=?, description=?, releaseDate=?, duration=?, mpa=? WHERE film_id=? ";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", film.getId());
        insertGenresInTable(film);
        Integer mpaId = film.getMpa().getId();
        film.setMpa(findMpaFilm(mpaId));
        film.setGenres(findGenresFilm(film));
        film.setRate(findRateFilm(film.getId()));
        return film;
    }

    @Override
    public Film findById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM films WHERE film_id=?",
                (resultSet, rowNum) -> filmParameters(resultSet), id);
    }

    @Override
    public boolean containsFilm(Integer id) {
        Long count = jdbcTemplate.queryForObject("select count(film_id) from films where film_id = ?", Long.class, id);
        return count == 1;
    }

    @Override
    public void likeFilm(Integer id, Integer userId) {
        jdbcTemplate.update(
                "INSERT INTO like_ids (film_id, user_id) VALUES (?, ?)", id, userId);
        findById(id).setRate(findById(id).getRate() + 1);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        jdbcTemplate.update(
                "DELETE FROM like_ids WHERE film_id=? and user_id=?", id, userId);
        if (findById(id).getRate() > 0) {
            findById(id).setRate(findById(id).getRate() - 1);
        }
    }

    @Override
    public List<Film> bestFilms(Integer count) {
        List<Film> films = jdbcTemplate.query(
                "SELECT * FROM films",
                (resultSet, rowNum) -> filmParameters(resultSet));
        return films.stream()
                .sorted((x1, x2) -> x2.getRate() - x1.getRate())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film filmParameters(ResultSet resultSet) {
        try {
            Film film = new Film();
            film.setId(resultSet.getInt("film_id"));
            film.setName(resultSet.getString("film_name"));
            film.setDescription(resultSet.getString("description"));
            film.setReleaseDate(resultSet.getDate("releaseDate").toLocalDate());
            film.setDuration(resultSet.getInt("duration"));
            film.setMpa(findMpaFilm(resultSet.getInt("mpa")));
            film.setGenres(findGenresFilm(film));
            film.setRate(findRateFilm(film.getId()));
            return film;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Integer findRateFilm(Integer id) {
        return this.jdbcTemplate.queryForObject("select count(user_id) from like_ids WHERE film_id=?", Integer.class, id);
    }

    public Mpa findMpaFilm(Integer mpaId) {
        return new Mpa(mpaId, jdbcTemplate.queryForObject(
                "SELECT mpa_name FROM mpa where mpa_id = ?",
                String.class, mpaId));
    }

    public List<Genre> findGenresFilm(Film film) {
        List<Integer> genresIds = jdbcTemplate.queryForList("SELECT film_genre.genre_id FROM film_genre WHERE film_id=?", Integer.class, film.getId());
        List<Genre> genres = new ArrayList<>();
        for (Integer j : genresIds) {
            Genre genre = new Genre();
            String str = jdbcTemplate.queryForObject("SELECT genre_name FROM genre WHERE genre_id=?", String.class, j);
            genre.setName(str);
            genre.setId(j);
            if (genres.contains(genre)) {
                break;
            }
            genres.add(genre);
        }
        return genres;
    }

    public void insertGenresInTable(Film film) {
        int[] genresIds = film.getGenres().stream().mapToInt(Genre::getId).toArray();
        for (int genreId : genresIds) {
            jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) VALUES (?,?)",
                    film.getId(), genreId);
        }
    }
}