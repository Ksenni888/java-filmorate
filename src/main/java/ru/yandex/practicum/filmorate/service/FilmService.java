package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NoInformationFoundException;
import ru.yandex.practicum.filmorate.exeption.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private static final String FILM_BIRTHDAY = "1895-12-28";
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film create(Film film) {
        if (film.getId() != 0) {
            throw new ValidationException("ID must be empty");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse(FILM_BIRTHDAY))) {
            throw new ValidationException("Time of release must be after" + FILM_BIRTHDAY);
        }
        return filmStorage.create(film);
    }

    public Film rewriteFilm(Film film) {
        if (!filmStorage.containsFilm(film.getId())) {
            throw new ObjectNotFoundException("There is no such film in the database");
        }

        if (film.getReleaseDate().isBefore(LocalDate.parse(FILM_BIRTHDAY))) {
            throw new ValidationException("Time of release must be after" + FILM_BIRTHDAY);
        }
        return filmStorage.rewriteFilm(film);
    }

    public Film getFilmsById(Integer id) {
        if (!filmStorage.containsFilm(id)) {
            throw new ObjectNotFoundException(String.format("Film with id=%d not found", id));
        }
        log.info("Info about film id=" + id);
        return filmStorage.findById(id);
    }

    public Set<Integer> likesFilm(Integer id, Integer userId) {
        if (!filmStorage.containsFilm(id)) {
            throw new NoInformationFoundException(String.format("Film with id=%d not found", id));
        }
        if (!userStorage.containsUser(userId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", userId));
        }
        filmStorage.findById(id).getLikeIds().add(userId);
        log.info("Liked the movie");
        return filmStorage.findById(id).getLikeIds();
    }

    public Set<Integer> deleteLike(Integer id, Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new ObjectNotFoundException("Film's and User's id must be over 0");
        }

        if (!filmStorage.containsFilm(id)) {
            throw new NoInformationFoundException(String.format("Film with id=%d not found", id));
        }
        if (!userStorage.containsUser(userId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", userId));
        }
        filmStorage.findById(id).getLikeIds().remove(userId);
        log.info("Like deleted");
        return filmStorage.findById(id).getLikeIds();
    }

    public List<Film> bestFilms(Integer count) {
        if (count <= 0) {
            throw new ValidationException("Count must be over 0");
        }
        return filmStorage.findAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikeIds().size() - f1.getLikeIds().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
