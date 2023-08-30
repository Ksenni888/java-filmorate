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
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.filmStorage = filmStorage;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film getFilmsByIdService(Integer id) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            log.warn(String.format("Film with id=%d not found", id));
            throw new ObjectNotFoundException(String.format("Film with id=%d not found", id));
        }
        log.info("Info about film id=" + id);
        return inMemoryFilmStorage.getFilms().get(id);
    }

    public Set<Integer> likesFilmService(Integer id, Integer userId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            log.warn(String.format("Film with id=%d not found", id));
            throw new NoInformationFoundException(String.format("Film with id=%d not found", id));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            log.warn(String.format("User with id=%d not found", userId));
            throw new NoInformationFoundException(String.format("User with id=%d not found", userId));
        }
        inMemoryFilmStorage.getFilms().get(id).getLikes().add(userId);
        log.info("Liked the movie");
        return inMemoryFilmStorage.getFilms().get(id).getLikes();
    }

    public Set<Integer> deleteLike(Integer id, Integer userId) {
        if (id <= 0 || userId <= 0) {
            log.warn("Film's and User's id must be over 0");
            throw new ObjectNotFoundException("Film's and User's id must be over 0");
        }

        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            log.warn(String.format("Film with id=%d not found", id));
            throw new NoInformationFoundException(String.format("Film with id=%d not found", id));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            log.warn(String.format("User with id=%d not found", userId));
            throw new NoInformationFoundException(String.format("User with id=%d not found", userId));
        }
        inMemoryFilmStorage.getFilms().get(id).getLikes().remove(userId);
        log.warn("Like deleted");
        return inMemoryFilmStorage.getFilms().get(id).getLikes();
    }

    public List<Film> bestFilmsService(Integer count) {
        if (count <= 0) {
            log.warn("Count must be over 0");
            throw new ValidationException("Count must be over 0");
        }
        return filmStorage.findAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
