package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final String FILM_BIRTHDAY = "1895-12-28";
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int unicId;

    public List<Film> findAllFilms() {
        return List.copyOf(films.values());
    }

    public Map<Integer, Film> getAllFilms() {
        return getFilms();
    }

    public Film create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("The film is recorded {}", film);
        return film;
    }

    public Film rewriteFilm(Film film) {
        films.replace(film.getId(), film);
        log.info("Info about film updated {}", film);
        return film;
    }

    public Film findById(Integer id) {
        return films.get(id);
    }

    public boolean containsFilm(Integer id) {
        return films.containsKey(id);
    }

    private int generateId() {
        return ++unicId;
    }
}
