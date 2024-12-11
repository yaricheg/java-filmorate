package ru.yandex.practicum.filmorate.dal.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.*;
import org.springframework.dao.DuplicateKeyException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    private static final String GET_MOST_POPULAR = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
            "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id " +
            "WHERE f.id IN (SELECT film_id " +
            "FROM likes " +
            "GROUP BY film_id " +
            "ORDER BY COUNT(user_id) DESC) " +
            "LIMIT ? ";

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film save(Film film) {
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
        saveGenres(film);
        return getFilmById(film.getId());
    }

    @Override
    public Film update(Film film) {
        jdbc.update("delete from FILM_GENRE where FILM_ID = ?", film.getId());
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
        saveGenres(film);
        return getFilmById(film.getId());
    }

    @Override
    public void delete(Film film) {
        String deleteFilmSql = "DELETE FROM films WHERE film_id = ?";
        jdbc.update(deleteFilmSql, film.getId());
    }

    @Override
    public Film getFilmById(Integer filmId) {
        Optional<Film> optionalFilm = findOne(GET_ALL_QUERY.concat(" WHERE f.id = ?"), filmId);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException("Фильм под id=%s не найден");
        }
        Film film = optionalFilm.get();
        List<Genre> genres = getAllFilmGenresByFilmId(filmId).stream().toList();
        film.setGenres(genres);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> films = findMany(GET_ALL_QUERY);
        return films;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        final String insertQuery = "INSERT INTO likes (film_id, user_id) values (?, ?)";
        final String increaseRateQuery = "UPDATE films SET rate = rate + 1 WHERE id = ?";
        jdbc.update(insertQuery, filmId, userId);
        jdbc.update(increaseRateQuery, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        final String deleteQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        final String decreaseRateQuery = "UPDATE films SET rate = rate - 1 WHERE id = ?";
        jdbc.update(deleteQuery, filmId, userId);
        jdbc.update(decreaseRateQuery, filmId);
    }


    @Override
    public Collection<Genre> getAllFilmGenresByFilmId(Integer filmId) {
        final String getAllByIdQuery = "SELECT g.id, g.name AS name FROM film_genre AS fg LEFT JOIN genres g ON " +
                "fg.genre_id = g.id WHERE film_id = ?";
        return jdbc.query(getAllByIdQuery, new GenreRowMapper(), filmId);
    }


    @Override
    public Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films) {
        final String getAllQuery = "SELECT fg.film_id, g.id AS genre_id, g.name AS name FROM film_genre fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (%s)";

        Map<Integer, List<Genre>> filmGenreMap = new HashMap<>();
        Collection<String> ids = films.stream()
                .map(film -> String.valueOf(film.getId()))
                .toList();

        jdbc.query(String.format(getAllQuery, String.join(",", ids)), rs -> {
            Genre genre = new Genre(rs.getInt("genre_id"),
                    rs.getString("name"));

            Integer filmId = rs.getInt("film_id");

            filmGenreMap.putIfAbsent(filmId, new ArrayList<>());
            filmGenreMap.get(filmId).add(genre);
        });
        return filmGenreMap;
    }

    @Override
    public Collection<Film> getMostPopular(Integer count) {
        return findMany(GET_MOST_POPULAR, count);
    }

    @Override
    public User getUserById(Integer userId) {
        final String getUserById = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbc.queryForObject(getUserById, new UserRowMapper(), userId);
        } catch (DataAccessException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }


    @Override
    public void deleteFilmGenres(Integer filmId) {
        final String deleteGenres = "delete from FILM_GENRE where FILM_ID = ?";
        jdbc.update(deleteGenres, filmId);
    }


    private void saveGenres(Film film) {
        try {
            final Integer filmId = film.getId();
            final List<Genre> genres = film.getGenres();
            if (genres == null || genres.isEmpty()) {
                return;
            }
            final ArrayList<Genre> genreList = new ArrayList<>(genres);
            jdbc.batchUpdate(
                    "insert into FILM_GENRE (FILM_ID, GENRE_ID) values (?, ?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, filmId);
                            ps.setInt(2, genreList.get(i).getId());
                        }

                        public int getBatchSize() {
                            return genreList.size();
                        }
                    });
        } catch (DuplicateKeyException e) {
            log.warn("Жанр уже существует");
        } catch (DataAccessException e) {
            throw new ValidationException("Введите правильный id жанра");
        }
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        return findMany(GET_MOST_POPULAR, count);
    }

    @Override
    public Collection<Film> getPopularFilmsSortedByGenre(Integer count, Integer genreId) {
        String sqlQuery = "SELECT " +
                "f.*, " +
                "m.id AS mpa_id, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "JOIN mpa m ON m.id = f.mpa_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, f.rate, mpa_id, mpa_name " +
                "ORDER BY COUNT(f.id) DESC " +
                "LIMIT ?";
        return findMany(sqlQuery, genreId, count);
    }

    @Override
    public Collection<Film> getPopularFilmsSortedByGenreAndYear(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT " +
                "f.*, " +
                "m.id AS mpa_id, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "JOIN mpa m ON m.id = f.mpa_id " +
                "WHERE (fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ?) " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, f.rate, mpa_id, mpa_name " +
                "ORDER BY COUNT(f.id) DESC " +
                "LIMIT ?";
        return findMany(sqlQuery, genreId, year, count);
    }

    @Override
    public Collection<Film> getPopularFilmsSortedByYear(Integer count, Integer year) {
        String sqlQuery = "SELECT " +
                "f.*, " +
                "m.id AS mpa_id, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "JOIN mpa m ON m.id = f.mpa_id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, f.rate, mpa_id, mpa_name " +
                "ORDER BY COUNT(f.id) DESC " +
                "LIMIT ?";
        return findMany(sqlQuery, year, count);
    }

}

