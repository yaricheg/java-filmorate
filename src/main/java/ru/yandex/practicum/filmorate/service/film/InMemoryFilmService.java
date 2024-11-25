package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.users.UserStorage;

import java.util.*;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public InMemoryFilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                               @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film saveFilm(Film film) {
        return filmStorage.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getFilms();
    }

    @Override
    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }


    @Override
    public void deleteFilm(Film film) {
        filmStorage.delete(film);
    }


    @Override
    public void addLike(Integer idFilm, Integer idUser) {
        filmStorage.addLike(filmStorage.getFilmById(idFilm), userStorage.getUserById(idUser));
    }

    @Override
    public void deleteLike(Integer idFilm, Integer idUser) {
        filmStorage.deleteLike(filmStorage.getFilmById(idFilm), userStorage.getUserById(idUser));
    }

    public Collection<Film> topFilms(Integer count) {
        return filmStorage.getMostPopular(count);
    }
}






