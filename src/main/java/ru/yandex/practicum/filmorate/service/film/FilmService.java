package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Film saveFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAll();

    Film getFilmById(Integer id);

    void deleteFilm(Film film);

    void addLike(Integer film, Integer user);

    void deleteLike(Integer film, Integer user);

    Collection<Film> topFilms(Integer count);

    List<Film> toFilmsDto(Collection<Film> films);
}
