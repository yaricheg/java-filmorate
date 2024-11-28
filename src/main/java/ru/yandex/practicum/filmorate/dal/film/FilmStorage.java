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

   // void addFilmGenre(Integer filmId, Integer genreId);

    Collection<Genre> getAllFilmGenresByFilmId(Integer filmId);

    //void deleteAllFilmGenresByFilmId(Integer filmId);

    Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films);


    List<Like> getLikesFilmId(Integer filmId);

    Collection<Film> getMostPopular(Integer count);

    Mpa getMpaById(Integer mpaId);

    User getUserById(Integer userId);

    int[] batchUpdateAddGenre(final List<Integer> genres, Integer filmId);

}
