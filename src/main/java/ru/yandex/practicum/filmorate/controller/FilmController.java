package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.time.LocalDate;
import java.util.Collection;


@AllArgsConstructor
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;
    private static final Integer LENGTH_DESCRIPTION = 200;
    private static final LocalDate FIRST_DATE_FILMS = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Просмотр всех фильмов");
        return filmStorage.getAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        checkFilm(film);
        filmStorage.save(film);
        log.info("Добавлен новый фильм {}.", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updateFilm) {
        if (filmStorage.getFilms().containsKey(updateFilm.getId())) {
            checkNullFilm(updateFilm);
            checkFilm(updateFilm);
            Film correctFilm = filmStorage.update(updateFilm);
            log.info("Обновлен фильм.", correctFilm);
            return correctFilm;
        }
        throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
    }

    @DeleteMapping
    public void delete(@RequestBody Film deleteFilm) {
        filmStorage.deleteFilm(deleteFilm);
        log.info("Удален фильм.", deleteFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");

        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}.", userId, id);

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");

        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        filmService.deleteLike(id, userId);
        log.info("Пользователь {} удалил лайк фильму {}.", userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> topFilms(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new ValidationException("Значение size должно быть больше нуля");
        }
        return filmService.topFilms(count);
    }


    private void checkFilm(Film film) {
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

    private void checkNullFilm(Film film) {
        if (film.getName() == null) {
            throw new NullPointerException("Значение null в поле названия фильма");
        }
        if (film.getDescription() == null) {
            throw new NullPointerException("Значение null в поле описания");
        }
        if (film.getReleaseDate() == null) {
            throw new NullPointerException("Значение null в даты релиза");
        }
        if (film.getDuration() == null) {
            throw new NullPointerException("Значение null в поле продолжительности фильма");
        }
    }
}

