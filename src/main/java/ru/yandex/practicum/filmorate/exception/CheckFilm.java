package ru.yandex.practicum.filmorate.exception;

import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;

public class CheckFilm {

    private static final Integer LENGTH_DESCRIPTION = 200;
    private static final LocalDate FIRST_DATE_FILMS = LocalDate.of(1895, 12, 28);

    public void checkFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > LENGTH_DESCRIPTION) {
            throw new ValidationException("Длина описания должна быть меньше 200 символов");
        }
        if (film.getReleaseDate().isBefore(FIRST_DATE_FILMS)) {
            throw new ValidationException("Дата выхода фильма должен быть не ранее 28.12.1895");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}
