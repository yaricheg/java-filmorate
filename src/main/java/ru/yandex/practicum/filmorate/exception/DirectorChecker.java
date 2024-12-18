package ru.yandex.practicum.filmorate.exception;

import ru.yandex.practicum.filmorate.model.Director;

public class DirectorChecker {

    public static void checkDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым!");
        }
    }
}
