package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
//@Builder
public class Film {
    private int id;

    @NotBlank(message = "The name cannot be empty")
    private String name;

    @Size(max = 200, message = "The description should be no more than 200 characters")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "The value must be positive")
    private int duration;

    private Set<Integer> likeIds = new HashSet<>();

    private List<Genre> genres = new ArrayList<>();

    private Mpa mpa;

    private int rate;
}