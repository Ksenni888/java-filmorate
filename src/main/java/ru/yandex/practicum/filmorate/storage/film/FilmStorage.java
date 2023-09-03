package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    public List<Film> findAllFilms();
    public Map<Integer, Film> findAllFilmsHashMap();

    public Film create(Film film);

    public Film rewriteFilm(Film film);

}
