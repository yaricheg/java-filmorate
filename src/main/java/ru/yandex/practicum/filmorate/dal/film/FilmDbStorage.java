package ru.yandex.practicum.filmorate.dal.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.dal.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.dal.likes.LikesStorage;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;

import java.util.*;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String GET_ALL_QUERY = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.id";
    private static final String INSERT_QUERY = "INSERT INTO films (name, release_date, description, " +
            "duration, mpa_id, rate) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, release_date = ?, description = ?, " +
            "duration = ?, mpa_id = ?, rate = ? WHERE id = ?";

    private static final String GET_MOST_POPULAR = "SELECT * FROM  films " +
            "WHERE id IN (SELECT film_id " +
            "FROM likes " +
            "GROUP BY film_id " +
            "ORDER BY COUNT(user_id) DESC)";


    private final FilmGenreStorage filmGenreStorage;
    private final MpaStorage mpaStorage;
    private final LikesStorage likesStorage;

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper,
                         FilmGenreStorage filmGenreStorage,
                         MpaStorage mpaStorage, LikesStorage likeStorage) {
        super(jdbc, mapper);
        this.filmGenreStorage = filmGenreStorage;
        this.mpaStorage = mpaStorage;
        this.likesStorage = likeStorage;
    }

    @Override
    public Film save(Film film) {
        if(mpaStorage.getMpaById(film.getMpa().getId()) == null){

        }
        Integer id = Math.toIntExact(insert(
                INSERT_QUERY,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate()
        ));
        film.setId(id);
        return addFields(film);
    }

    @Override
    public Film update(Film film) {
        filmGenreStorage.deleteAllFilmGenresByFilmId(film.getId());
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId()
        );
        return addFields(film);
    }

    @Override
    public void delete(Film film) {
        return;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        Optional<Film> optionalFilm = findOne(GET_ALL_QUERY.concat(" WHERE f.id = ?"), filmId);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException("Фильм под id=%s не найден");
        } else {
            return addFields(optionalFilm.get());
        }
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> films = findMany(GET_ALL_QUERY);
        return setMultipleFilmsFields(films);
    }

    @Deprecated
    @Override
    public Film addLike(Film film, User user) {
        return null;
    }

    @Deprecated
    @Override
    public void deleteLike(Film film, User user) {
    }


    @Override
    public Collection<Film> getMostPopular(Integer count) {
        Collection<Film> films = findMany(GET_ALL_QUERY.concat(" WHERE f.id IN (SELECT film_id " +
                "FROM likes " +
                "GROUP BY film_id " +
                "ORDER BY COUNT(user_id) DESC)")); //.concat(" LIMIT 1000")
        return setMultipleFilmsFields(films);
    }


   private Film addFields(Film film) {
        Integer filmId = film.getId();
        Integer mpaId = film.getMpa().getId();
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> filmGenreStorage.addFilmGenre(filmId, genre.getId()));
        }
        List<Genre> filmGenres = (List<Genre>) filmGenreStorage.getAllFilmGenresByFilmId(film.getId());
        Mpa filmMpa = mpaStorage.getMpaById(mpaId).get();
        List<Like> filmLikes = (List<Like>) likesStorage.getLikesFilmId(filmId);
        return film.toBuilder().mpa(filmMpa).genres(filmGenres).likes(filmLikes).build();
    }

    private List<Film> setMultipleFilmsFields(Collection<Film> films) {
        Map<Integer, List<Genre>> filmGenresMap = filmGenreStorage.getAllFilmGenres(films);
        films.forEach(film -> {
            Integer filmId = film.getId();
            film.setGenres((List<Genre>) filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
            film.setLikes(likesStorage.getLikesFilmId(filmId));
        });
        return (List<Film>) films;
    }
}