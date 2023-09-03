package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmStorage.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmsById(@PathVariable Integer id) {
        log.info("Get information about film id=" + id);
        return filmService.getFilmsById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Create new film");
        return filmStorage.create(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseBody
    public Set<Integer> likesFilm(@PathVariable Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int userId = Integer.parseInt(pathVarsMap.get("userId"));
        log.info("Like the movie");
        return filmService.likesFilm(id, userId);
    }

    @PutMapping
    public Film rewriteFilm(@Valid @RequestBody Film film) {
        log.info("Rewrite the movie");
        return filmStorage.rewriteFilm(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseBody
    public Set<Integer> deliteLike(@PathVariable Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int userId = Integer.parseInt(pathVarsMap.get("userId"));
        log.info("Delete like the movie");
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseBody
    public List<Film> bestFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count == null) return filmService.bestFilms(10);
        log.info("Show best films");
        return filmService.bestFilms(count);
    }
}

