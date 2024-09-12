package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Integer LENGTHDESCRIPTION = 200;
    private static final LocalDate FIRSTDATEFILMS = LocalDate.of(1895, 12, 28);

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
            if (!(updateFilm.getDescription().isEmpty())) {
                oldFilm.setDescription(updateFilm.getDescription());
                log.info("Описание обновлено");
            }
            if (!(updateFilm.getReleaseDate().isEmpty())) {
                oldFilm.setReleaseDate(updateFilm.getReleaseDate());
                log.info("Дата обновлена");
            }
            if (!(updateFilm.getDuration().isEmpty())) {
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
        if (film.getDescription().get().length() > LENGTHDESCRIPTION) {
            throw new ValidationException("Длина описания должна быть меньше 200 символов");
        }
        if (film.getReleaseDate().get().isBefore(FIRSTDATEFILMS)) {
            throw new ValidationException("Дата выхода фильма должен быть не ранее 28.12.1895");
        }
        if (film.getDuration().get() < 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}

