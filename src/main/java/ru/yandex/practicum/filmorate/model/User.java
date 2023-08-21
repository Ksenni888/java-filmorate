package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    private String email;

    @NotBlank(message = "The login cannot be empty")
    private String login;

    private String name;

    @Past(message = "The date of birth cannot be in the future")
    private LocalDate birthday;
}