package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    void delete(Integer id);

    Film getFilmById(Integer id);

    Collection<Film> getFilms();

    Collection<Film> getFilmsByIdDirectorSortYear(int id);

    Collection<Film> getFilmsByIdDirectorsSortLike(int id);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    Collection<Genre> getAllFilmGenresByFilmId(Integer filmId);

    Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films);

    Map<Integer, List<Director>> getAllFilmDirectors(Collection<Film> films);

    Collection<Film> getMostPopular(Integer count);

    void deleteFilmGenres(Integer filmId);

    User getUserById(Integer userId);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);

    Collection<Film> searchFilms(String query, String by);

}
