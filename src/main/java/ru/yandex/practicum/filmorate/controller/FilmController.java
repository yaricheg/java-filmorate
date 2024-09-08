package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        checkFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Пользователь добавил новый фильм {}.", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updateFilm) throws ValidationException, NotFoundException {
        Film oldFilm;
        if (updateFilm.getId() == null) {
            throw new ValidationException("Введите id");
        }
        checkFilm(updateFilm);
        if (films.containsKey(updateFilm.getId())) {
            oldFilm = films.get(updateFilm.getId());
            if (!(updateFilm.getDescription() == null || updateFilm.getDescription().isBlank())) {
                oldFilm.setDescription(updateFilm.getDescription());
                log.info("Описание обновлено");
            }
            if (!(updateFilm.getReleaseDate() == null)) {
                oldFilm.setReleaseDate(updateFilm.getReleaseDate());
                log.info("Дата обновлена");
            }
            if (!(updateFilm.getDuration() == null)) {
                oldFilm.setDuration(updateFilm.getDuration());
                log.info("Продолжительность фильма обновлена");
            }
            oldFilm.setName(updateFilm.getName());
            log.info("Название фильма обновлено");
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длина описания должна быть меньше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выхода фильма должен быть не ранее 28.12.1895");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}

