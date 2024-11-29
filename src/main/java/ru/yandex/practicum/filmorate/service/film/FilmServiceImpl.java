package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
        return toFilmDto(filmStorage.save(film));
    }

    @Override
    public Film updateFilm(Film film) {
        return toFilmDto(filmStorage.update(film));
    }

    @Override
    public Collection<Film> getAll() {
        return toFilmsDto(filmStorage.getFilms()); // toFilmsDto
    }

    @Override
    public Film getFilmById(Integer id) {
        return toFilmDto(filmStorage.getFilmById(id));
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

    private Film toFilmDto(Film film) {
        Integer filmId = film.getId();
        if (film.getGenres() != null) {
            filmStorage.deleteFilmGenres(film.getId());
            List<Integer> genresId = film.getGenres().stream()
                    .map(genre -> genre.getId())
                    .collect(Collectors.toList());
            filmStorage.batchUpdateAddGenre(genresId, filmId);
        }
        List<Genre> filmGenres = (List<Genre>) filmStorage.getAllFilmGenresByFilmId(film.getId());

        return film.toBuilder().genres(filmGenres).build();
    }


    private List<Film> toFilmsDto(Collection<Film> films) {
        Map<Integer, List<Genre>> filmGenresMap = filmStorage.getAllFilmGenres(films);
        films.forEach(film -> {
            Integer filmId = film.getId();
            film.setGenres(filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
        });
        return (List<Film>) films;
    }

}






