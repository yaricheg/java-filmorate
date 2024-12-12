package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Film saveFilm(Film film) {
        if (film.getMpa().getId() > 5 || film.getMpa().getId() < 0) {
            throw new ValidationException("Введен неправильный id рейтинга");
        }
        return filmStorage.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Collection<Film> getAll() {
        return toFilmsDto(filmStorage.getFilms()); // toFilmsDto
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
        Film film = filmStorage.getFilmById(idFilm);
        User user = filmStorage.getUserById(idUser);
        filmStorage.addLike(film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Integer idFilm, Integer idUser) {
        Film film = filmStorage.getFilmById(idFilm);
        User user = filmStorage.getUserById(idUser);
        filmStorage.deleteLike(film.getId(), user.getId());
    }

    public Collection<Film> topFilms(Integer count) {
        return toFilmsDto(filmStorage.getMostPopular(count));
    }

    @Override
    public List<Film> toFilmsDto(Collection<Film> films) {
        Map<Integer, List<Genre>> filmGenresMap = filmStorage.getAllFilmGenres(films);
        films.forEach(film -> {
            Integer filmId = film.getId();
            film.setGenres(filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
        });
        return (List<Film>) films;
    }

}






