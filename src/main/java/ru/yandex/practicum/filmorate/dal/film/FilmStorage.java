package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    void delete(Film film);

    Film getFilmById(Integer id);

    Collection<Film> getFilms();

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    Collection<Genre> getAllFilmGenresByFilmId(Integer filmId);

    Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films);

    Collection<Film> getMostPopular(Integer count);

    void deleteFilmGenres(Integer filmId);

    User getUserById(Integer userId);

    Collection<Film> getMostPopularFilms(Integer count);

    Collection<Film> getPopularFilmsSortedByGenre(Integer count, Integer genreId);

    Collection<Film> getPopularFilmsSortedByGenreAndYear(Integer count, Integer genreId, Integer year);

    Collection<Film> getPopularFilmsSortedByYear(Integer count, Integer year);

}
