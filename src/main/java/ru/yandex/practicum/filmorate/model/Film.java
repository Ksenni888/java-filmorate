package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "The name cannot be empty")
    private String name;

    @Size(max = 200, message = "The description should be no more than 200 characters")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "The value must be positive")
    private int duration;
}