package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public MpaController(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    @GetMapping
    public List<Mpa> getMpa() {
        log.info("List of movie genre");
        return filmDbStorage.getMpa();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Mpa getMpaById(@PathVariable Integer id) {
        log.info("Get mpa by id=" + id);
        return filmDbStorage.getMpaById(id);
    }
}
