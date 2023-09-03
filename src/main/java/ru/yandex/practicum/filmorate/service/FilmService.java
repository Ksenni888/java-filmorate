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
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserStorage userStorage) {
        this.filmStorage = inMemoryFilmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilmsById(Integer id) {
        if (!filmStorage.findAllFilmsHashMap().containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Film with id=%d not found", id));
        }
        log.info("Info about film id=" + id);
        return filmStorage.findAllFilmsHashMap().get(id);
    }

    public Set<Integer> likesFilm(Integer id, Integer userId) {
        if (!filmStorage.findAllFilmsHashMap().containsKey(id)) {
            throw new NoInformationFoundException(String.format("Film with id=%d not found", id));
        }
        if (!userStorage.findAllUsersHashMap().containsKey(userId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", userId));
        }
        filmStorage.findAllFilmsHashMap().get(id).getLikeId().add(userId);
        log.info("Liked the movie");
        return filmStorage.findAllFilmsHashMap().get(id).getLikeId();
    }

    public Set<Integer> deleteLike(Integer id, Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new ObjectNotFoundException("Film's and User's id must be over 0");
        }

        if (!filmStorage.findAllFilmsHashMap().containsKey(id)) {
            throw new NoInformationFoundException(String.format("Film with id=%d not found", id));
        }
        if (!userStorage.findAllUsersHashMap().containsKey(userId)) {
            throw new NoInformationFoundException(String.format("User with id=%d not found", userId));
        }
        filmStorage.findAllFilmsHashMap().get(id).getLikeId().remove(userId);
        log.info("Like deleted");
        return filmStorage.findAllFilmsHashMap().get(id).getLikeId();
    }

    public List<Film> bestFilms(Integer count) {
        if (count <= 0) {
            throw new ValidationException("Count must be over 0");
        }
        return filmStorage.findAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikeId().size() - f1.getLikeId().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
