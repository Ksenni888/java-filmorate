package ru.yandex.practicum.filmorate.exeption;

public class NoInformationFoundException extends RuntimeException {
    public NoInformationFoundException(String message) {
        super(message);
    }
}
