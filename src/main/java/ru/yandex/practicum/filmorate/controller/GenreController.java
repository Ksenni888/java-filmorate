package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;
    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genre> allGenres() {
        log.info("List of movie genre");
        return filmService.getGenres();
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Genre getGenreById(@PathVariable Integer id){
        log.info("Get genre by id=" + id);
        return filmService.getGenreById(id);
    }


}
