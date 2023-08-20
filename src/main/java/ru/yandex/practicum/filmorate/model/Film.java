package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание должно быть не более 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Значение должно быть положительным")
    private int duration;
}