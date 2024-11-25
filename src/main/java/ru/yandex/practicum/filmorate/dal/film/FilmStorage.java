package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    void delete(Film film);

    Film getFilmById(Integer id);

    Collection<Film> getFilms();

    Film addLike(Film film, User user);

    void deleteLike(Film film, User user);

    Collection<Film> getMostPopular(Integer count);

}
