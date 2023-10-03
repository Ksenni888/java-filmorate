package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {

    public List<Film> findAllFilms();

    public Film create(Film film);

    public Film rewriteFilm(Film film);

    public Film findById(Integer id);

    public boolean containsFilm(Integer id);

    public void likeFilm(Integer id, Integer userId);

    public void deleteLike(Integer id, Integer userId);

    public List<Film> bestFilms(Integer count);

    public List<Genre> getGenres();

    public Genre getGenreById(Integer id);

    public boolean containsGenre(Integer id);
}
