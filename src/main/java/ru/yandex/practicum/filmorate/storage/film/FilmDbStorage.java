package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        List<Film> filmBack = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films");

        while (filmRows.next()) {
            Film film = new Film();
            filmParameters(film, filmRows);
            filmBack.add(film);
        }
        return filmBack;
    }

    @Override
    public Film create(Film film) {
        Integer idCount = jdbcTemplate.queryForObject("SELECT count(film_id) FROM films", Integer.class);
        if (idCount != null) {
            idCount = idCount + 1;
            film.setId(idCount);
        }
        jdbcTemplate.update(
                "INSERT INTO films (film_id, film_name, description, releaseDate, duration, mpa) VALUES (?, ?, ?, ?, ?, ?)",
                idCount, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        int[] genresIds = film.getGenres().stream().mapToInt(Genre::getId).toArray();

        for (int genreId : genresIds) {
            jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) values (?,?)",
                    film.getId(), genreId);
        }
        Integer mpaId = film.getMpa().getId();
        film.setMpa(findMpaFilm(mpaId));
        film.setGenres(findGenresFilm(film));
        return film;
    }

    @Override
    public Film rewriteFilm(Film film) {
        String sqlQuery = "UPDATE films SET film_name=?, description=?, releaseDate=?, duration=?, mpa=? WHERE film_id=? ";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        int[] genresIds = film.getGenres().stream().mapToInt(Genre::getId).toArray();

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", film.getId());

        for (int genreId : genresIds) {
            jdbcTemplate.update(
                    "INSERT INTO film_genre(film_id, genre_id) VALUES (?,?)",
                    film.getId(), genreId);
        }
        Integer mpaId = film.getMpa().getId();
        film.setMpa(findMpaFilm(mpaId));
        film.setGenres(findGenresFilm(film));
        film.setRate(findRateFilm(film));
        return film;
    }

    @Override
    public Film findById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id=?", id);
        Film film = new Film();
        if (filmRows.next()) {
            filmParameters(film, filmRows);
        }
        return film;
    }

    @Override
    public boolean containsFilm(Integer id) {
        try {
            Long count = jdbcTemplate.queryForObject("select count(film_id) from films where film_id = ?", Long.class, id);
            return count == 1;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(String.format("Film with id=%d not found", id));
        }
    }

    @Override
    public void likeFilm(Integer id, Integer userId) {
        jdbcTemplate.update(
                "INSERT INTO like_ids (film_id, user_id) VALUES (?, ?)",
                id, userId);
        findById(id).setRate(findById(id).getRate() + 1);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        jdbcTemplate.update(
                "DELETE FROM like_ids WHERE film_id=? and user_id=?",
                id, userId);
        if (findById(id).getRate() > 0) {
            findById(id).setRate(findById(id).getRate() - 1);
        }
    }

    @Override
    public List<Film> bestFilms(Integer count) {
        List<Film> filmBack = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * from films");
        while (filmRows.next()) {
            Film film = new Film();
            filmParameters(film, filmRows);
            filmBack.add(film);
        }
        return filmBack.stream()
                .sorted((x1, x2) -> x2.getRate() - x1.getRate())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void filmParameters(Film film, SqlRowSet filmRows) {
        film.setId(filmRows.getInt("film_id"));
        film.setName(filmRows.getString("film_name"));
        film.setDescription(filmRows.getString("description"));
        film.setReleaseDate(Objects.requireNonNull(filmRows.getDate("releaseDate")).toLocalDate());
        film.setDuration(filmRows.getInt("duration"));
        Integer mpaId = filmRows.getInt("mpa");
        film.setMpa(findMpaFilm(mpaId));
        film.setGenres(findGenresFilm(film));
        film.setRate(findRateFilm(film));
    }

    public int findRateFilm(Film film) {
        SqlRowSet rateRows = jdbcTemplate.queryForRowSet("SELECT user_id FROM like_ids WHERE film_id=?", film.getId());
        int i = 0;
        while (rateRows.next()) {
            i++;
        }
        return i;
    }

    public Mpa findMpaFilm(Integer mpaId) {
        SqlRowSet mpaRequest = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpa_id=?", mpaId);
        Mpa mpa = new Mpa();
        if (mpaRequest.next()) {
            mpa.setName(mpaRequest.getString("mpa_name"));
            mpa.setId(mpaRequest.getInt("mpa_id"));

        }
        return mpa;
    }

    public List<Genre> findGenresFilm(Film film) {
        SqlRowSet genresRequest = jdbcTemplate.queryForRowSet("SELECT genre_id FROM film_genre WHERE film_id=?", film.getId());
        List<Genre> genresBack = new ArrayList<>();
        while (genresRequest.next()) {
            Genre genre = new Genre();
            genre.setId(genresRequest.getInt("genre_id"));
            Integer genreId = genresRequest.getInt("genre_id");
            SqlRowSet requestGenreName = jdbcTemplate.queryForRowSet("SELECT genre_name FROM genre WHERE genre_id=?", genreId);
            if (requestGenreName.next()) {
                genre.setName(requestGenreName.getString("genre_name"));
                if (genresBack.contains(genre)) {
                    break;
                }
                genresBack.add(genre);
            }
            film.setGenres(genresBack);
        }
        return genresBack;
    }
}