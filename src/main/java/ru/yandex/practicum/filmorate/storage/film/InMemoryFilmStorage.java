package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    @Getter
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int unicId;
    private static final String FILM_BIRTHDAY = "1895-12-28";

    public List<Film> findAllFilms() {
        return List.copyOf(films.values());
    }

    public Film create(Film film) {

        if (film.getId() != 0) {
            log.warn("ID must be empty");
            throw new ValidationException("ID must be empty");
        }

        if (film.getReleaseDate().isBefore(LocalDate.parse(FILM_BIRTHDAY))) {
            log.warn("Time of release must be after" + FILM_BIRTHDAY);
            throw new ValidationException("Time of release must be after" + FILM_BIRTHDAY);
        }

        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("The film is recorded {}", film);
        return film;
    }

    public Film rewriteFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("There is no such film in the database");
            throw new ObjectNotFoundException("There is no such film in the database");
        }

        if (film.getReleaseDate().isBefore(LocalDate.parse(FILM_BIRTHDAY))) {
            log.warn("Time of release must be after" + FILM_BIRTHDAY);
            throw new ValidationException("Time of release must be after" + FILM_BIRTHDAY);
        }

        films.replace(film.getId(), film);
        log.info("Info about film updated {}", film);
        return film;
    }

    private int generateId() {
        return ++unicId;
    }
}
