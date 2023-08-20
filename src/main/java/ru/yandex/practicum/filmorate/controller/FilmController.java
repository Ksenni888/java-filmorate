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

    @GetMapping
    public List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        if (film.getId() == 0) {
            film.setId(generateId());

        } else {
            log.warn("id должен быть пустым");
            throw new ValidationException("id должен быть пустым");
        }


        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.warn("Время релиза не раньше 28.12.1895");
            throw new ValidationException("Время релиза не раньше 28.12.1895");

        } else {
            films.put(film.getId(), film);
            log.info("Записан фильм {}", film);
            return film;
        }
    }


    @PutMapping
    public Film rewriteFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Такого фильма нет в базе");
            throw new ValidationException("Такого фильма нет в базе");

        } else if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.warn("Время релиза не раньше 28.12.1895");
            throw new ValidationException("Время релиза не раньше 28.12.1895");

        } else {
            films.replace(film.getId(), film);
            log.info("Информация по фильму обновлена {}", film);
            return film;
        }
    }


    private int generateId() {
        return ++unicId;
    }
}

