package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    public void deleteFilm(Integer id) {
        filmStorage.delete(id);
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

    @Override
    public Collection<Film> topFilms(Integer count) {
        return toFilmsDto(filmStorage.getMostPopular(count));
    }

    @Override
    public Collection<Film> getFilmsByIdDirectorSortYear(int id) {
        return toFilmsDto(filmStorage.getFilmsByIdDirectorSortYear(id));
    }

    @Override
    public Collection<Film> getFilmsByIdDirectorsSortLike(int id) {
        return toFilmsDto(filmStorage.getFilmsByIdDirectorsSortLike(id));
    }

    @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        return toFilmsDto(filmStorage.getCommonFilms(userId, friendId));
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {
        return toFilmsDto(filmStorage.searchFilms(query, by));
    }

    private List<Film> toFilmsDto(Collection<Film> films) {
        Map<Integer, List<Genre>> filmGenresMap = filmStorage.getAllFilmGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = filmStorage.getAllFilmDirectors(films);
        films.forEach(film -> {
            Integer filmId = film.getId();
            film.setGenres(filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
            film.setDirectors(filmDirectorsMap.getOrDefault(filmId, new ArrayList<>()));
        });
        return (List<Film>) films;
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        return toFilmsDto(filmStorage.getMostPopularFilms(count));
    }

    @Override
    public Collection<Film> getPopularFilmsSortedByGenre(Integer count, Integer genreId) {
        return toFilmsDto(filmStorage.getPopularFilmsSortedByGenre(count, genreId));
    }

    @Override
    public Collection<Film> getPopularFilmsSortedByGenreAndYear(Integer count, Integer genreId, Integer year) {
        return toFilmsDto(filmStorage.getPopularFilmsSortedByGenreAndYear(count, genreId, year));
    }

    @Override
    public Collection<Film> getPopularFilmsSortedByYear(Integer count, Integer year) {
        return toFilmsDto(filmStorage.getPopularFilmsSortedByYear(count, year));
    }

}






