package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int unicId;
    private static final String DATE = "1895-12-28";

    @GetMapping
    public List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        if (film.getId() != 0) {
            log.warn("ID must be empty");
            throw new ValidationException("ID must be empty");
        }

        if (film.getReleaseDate().isBefore(LocalDate.parse(DATE))) {
            log.warn("Time of release must be after" + DATE);
            throw new ValidationException("Time of release must be after" + DATE);
        }

        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("The film is recorded {}", film);
        return film;
    }

    @PutMapping
    public Film rewriteFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("There is no such film in the database");
            throw new ValidationException("There is no such film in the database");
        }

        if (film.getReleaseDate().isBefore(LocalDate.parse(DATE))) {
            log.warn("Time of release must be after" + DATE);
            throw new ValidationException("Time of release must be after" + DATE);
        }

        films.replace(film.getId(), film);
        log.info("Info about film updated {}", film);
        return film;
    }

    private int generateId() {
        return ++unicId;
    }
}

