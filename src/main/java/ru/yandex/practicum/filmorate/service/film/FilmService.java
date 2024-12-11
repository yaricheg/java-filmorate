package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Film saveFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAll();

    Film getFilmById(Integer id);

    void deleteFilm(Film film);

    void addLike(Integer film, Integer user);

    void deleteLike(Integer film, Integer user);

    Collection<Film> topFilms(Integer count);

    Collection<Film> getMostPopularFilms(Integer count);

    Collection<Film> getPopularFilmsSortedByGenre(Integer count, Integer genreId);

    Collection<Film> getPopularFilmsSortedByGenreAndYear(Integer count, Integer genreId, Integer year);

    Collection<Film> getPopularFilmsSortedByYear(Integer count, Integer year);
}
