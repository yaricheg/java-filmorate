package ru.yandex.practicum.filmorate.exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String massage) {
        super(massage);
    }
}
