package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmChecker;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Просмотр всех фильмов");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.debug("Просмотр всех фильмов");
        return filmService.getFilmById(id);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmByDirector(@PathVariable Integer directorId,
                                              @RequestParam String sortBy) {
        if (sortBy.equals("year")) {
            log.debug("Возвращаем фильмы режиссера с id {} с сортировкой по годам", directorId);
            return filmService.getFilmsByIdDirectorSortYear(directorId);
        }
        log.debug("Возвращаем фильмы режиссера с id {} с сортировкой по лайкам", directorId);
        return filmService.getFilmsByIdDirectorsSortLike(directorId);
    }


    @PostMapping
    public Film create(@RequestBody Film film) {
        FilmChecker.checkFilm(film);
        filmService.saveFilm(film);
        log.info("Добавлен новый фильм {}.", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updateFilm) {
        FilmChecker.checkFilm(updateFilm);
        Film correctFilm = filmService.updateFilm(updateFilm);
        log.info("Обновлен фильм.", correctFilm);
        return correctFilm;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        filmService.deleteFilm(id);
        log.info("Удален фильм.", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}.", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
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

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.debug("Просмотр всех общих фильмов");
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(
            @RequestParam String query,
            @RequestParam(defaultValue = "title,director") String by) {

        return filmService.searchFilms(query, by);
    }
}

