package ru.yandex.practicum.filmorate.dal.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.LikeMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.*;

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

    private static final String GET_MOST_POPULAR = "SELECT * FROM  films " +
            "WHERE id IN (SELECT film_id " +
            "FROM likes " +
            "GROUP BY film_id " +
            "ORDER BY COUNT(user_id) DESC) " +
            "LIMIT ? ";

   /* private static final String GET_MOST_POPULAR_BY_RATE = "SELECT * FROM films " +
            "ORDER BY rate DESC " +
            "LIMIT ? ";*/

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film save(Film film) {
        if (film.getMpa().getId() > 5 || film.getMpa().getId() < 0) {
            throw new ValidationException("Введен неправильный id рейтинга");
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
        return film;
    }

    @Override
    public Film update(Film film) {
       // deleteAllFilmGenresByFilmId(film.getId());
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
        return film;
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
        return optionalFilm.get();
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
        Film film = getFilmById(filmId);
        User user = getUserById(userId);
        jdbc.update(insertQuery, film.getId(), user.getId());
        jdbc.update(increaseRateQuery, film.getId());
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        final String deleteQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        final String decreaseRateQuery = "UPDATE films SET rate = rate - 1 WHERE id = ?";
        Film film = getFilmById(filmId);
        User user = getUserById(userId);
        jdbc.update(deleteQuery, film.getId(), user.getId());
        jdbc.update(decreaseRateQuery, film.getId());
    }


    @Override
    public Collection<Genre> getAllFilmGenresByFilmId(Integer filmId) {
        final String getAllByIdQuery = "SELECT g.id AS id, name FROM film_genre AS fg LEFT JOIN genres g ON " +
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
    public List<Like> getLikesFilmId(Integer filmId) {
        final String getLikesByFilmId = "SELECT * FROM likes WHERE film_id = ?";
        return jdbc.query(getLikesByFilmId, new LikeMapper(), filmId);
    }


    @Override
    public Mpa getMpaById(Integer mpaId) {
        final String getRateByMpaId = "SELECT * FROM mpa WHERE id = ?";
        return jdbc.queryForObject(getRateByMpaId, new MpaRowMapper(), mpaId);
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
    public int[] batchUpdateAddGenre(final List<Integer> genres, Integer filmId) {
        try {
            return this.jdbc.batchUpdate(
                    "INSERT INTO film_genre (film_id, genre_id) values (?, ?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Integer genre = genres.get(i);
                            ps.setInt(1, filmId);
                            ps.setInt(2, genre);
                        }
                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        } catch (DuplicateKeyException e) {
            log.warn("Жанр уже существует");
        } catch (DataAccessException e) {
            throw new ValidationException("Введите правильный id жанра");
        }
        return new int[0];
    }

}

