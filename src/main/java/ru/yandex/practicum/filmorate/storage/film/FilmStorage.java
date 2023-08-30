package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    public List<Film> findAllFilms();

    public Film create(Film film);

    public Film rewriteFilm(Film film);

}
