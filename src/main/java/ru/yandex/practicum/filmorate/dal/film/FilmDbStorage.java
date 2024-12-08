package ru.yandex.practicum.filmorate.dal.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
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

    private static final String GET_MOST_POPULAR = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name, COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "GROUP BY f.id, m.id, m.name " +
            "ORDER BY likes_count DESC, f.id " +
            "LIMIT ?; ";

    private static final String GET_COMMON_FILMS = " SELECT f.*, m.id AS mpa_id, m.name AS mpa_name FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id " +
            "JOIN likes l ON f.id = l.film_id " +
            "WHERE l.user_id = ? OR l.user_id = ? " +
            "GROUP BY f.id, m.id, m.name " +
            "HAVING COUNT(DISTINCT l.user_id) = 2 " +
            "ORDER BY COUNT(l.user_id) DESC";

    private static final String GET_DIRECTOR_ID_SORT_YEAR = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
            "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id " +
            "WHERE f.ID IN " +
            "(SELECT fd.film_id " +
            "FROM FILM_DIRECTORS fd " +
            "WHERE fd.director_id = ?) " +
            "ORDER BY RELEASE_DATE";

    private static final String GET_DIRECTOR_ID_SORT_LIKE = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
            "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id " +
            "WHERE f.id IN (SELECT film_id " +
            "FROM likes " +
            "GROUP BY film_id " +
            "ORDER BY COUNT(user_id) DESC) " +
            "AND f.id IN (SELECT fd.film_id " +
            "FROM FILM_DIRECTORS fd " +
            "WHERE fd.director_id = ?)";


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
        saveDirectors(film);
        return getFilmById(film.getId());
    }

    @Override
    public Film update(Film film) {
        jdbc.update("delete from FILM_GENRE where FILM_ID = ?", film.getId());
        jdbc.update("delete from FILM_DIRECTORS where FILM_ID = ?", film.getId());
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
        saveDirectors(film);
        return getFilmById(film.getId());
    }

    @Override
    public void delete(Integer id) {
        String deleteFilmSql = "DELETE FROM films WHERE id = ?";
        jdbc.update(deleteFilmSql, id);
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
        List<Director> directors = getAllFilmDirectorsByFilmId(filmId).stream().toList();
        film.setDirectors(directors);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return findMany(GET_ALL_QUERY);
    }

    @Override
    public Collection<Film> getFilmsByIdDirectorSortYear(int id) {
        return findMany(
                GET_DIRECTOR_ID_SORT_YEAR,
                id);
    }

    @Override
    public Collection<Film> getFilmsByIdDirectorsSortLike(int id) {
        return findMany(
                GET_DIRECTOR_ID_SORT_LIKE,
                id
        );
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        final String insertQuery = "INSERT INTO likes (film_id, user_id) values (?, ?)";
        final String increaseRateQuery = "UPDATE films SET rate = rate + 1 WHERE id = ?";
        jdbc.update(insertQuery, filmId, userId);
        jdbc.update(increaseRateQuery, filmId);
        addEvent(userId, "LIKE", "ADD", filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        final String deleteQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        final String decreaseRateQuery = "UPDATE films SET rate = rate - 1 WHERE id = ?";
        jdbc.update(deleteQuery, filmId, userId);
        jdbc.update(decreaseRateQuery, filmId);
        addEvent(userId, "LIKE", "REMOVE", filmId);
    }


    @Override
    public Collection<Genre> getAllFilmGenresByFilmId(Integer filmId) {
        final String getAllByIdQuery = "SELECT g.id, g.name AS name FROM film_genre AS fg LEFT JOIN genres g ON " +
                "fg.genre_id = g.id WHERE film_id = ?";
        return jdbc.query(getAllByIdQuery, new GenreRowMapper(), filmId);
    }

    private Collection<Director> getAllFilmDirectorsByFilmId(Integer filmId) {
        final String getAllByIdQuery = "SELECT d.id, d.name AS name FROM film_directors AS fd LEFT JOIN directors d ON " +
                "fd.director_id = d.id WHERE film_id = ?";
        return jdbc.query(getAllByIdQuery, new DirectorRowMapper(), filmId);
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
    public Map<Integer, List<Director>> getAllFilmDirectors(Collection<Film> films) {
        final String getAllQuery = "SELECT fd.film_id, d.id AS director_id, d.name AS name FROM film_directors fd " +
                "LEFT JOIN directors d ON fd.director_id = d.id WHERE fd.film_id IN (%s)";

        Map<Integer, List<Director>> filmDirectorMap = new HashMap<>();
        Collection<String> ids = films.stream()
                .map(film -> String.valueOf(film.getId()))
                .toList();

        jdbc.query(String.format(getAllQuery, String.join(",", ids)), rs -> {
            Director director = new Director(rs.getInt("director_id"),
                    rs.getString("name"));

            Integer filmId = rs.getInt("film_id");

            filmDirectorMap.putIfAbsent(filmId, new ArrayList<>());
            filmDirectorMap.get(filmId).add(director);
        });
        return filmDirectorMap;
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

    @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        return jdbc.query(GET_COMMON_FILMS, new FilmRowMapper(), userId, friendId);
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

    private void addEvent(Integer userId, String eventType, String operation, Integer entityId) {
        String sql = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";

        long timestamp = Instant.now().toEpochMilli();

        Event event = Event.builder()
                .timestamp(timestamp)
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();

        jdbc.update(sql, timestamp, userId, eventType, operation, entityId);
    }


    private void saveDirectors(Film film) {
        try {
            final Integer filmId = film.getId();
            final List<Director> directors = film.getDirectors();
            if (directors == null || directors.isEmpty()) {
                return;
            }
            final ArrayList<Director> directorsList = new ArrayList<>(directors);
            jdbc.batchUpdate(
                    "insert into FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) values (?, ?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, filmId);
                            ps.setInt(2, directorsList.get(i).getId());
                        }

                        public int getBatchSize() {
                            return directorsList.size();
                        }
                    });
        } catch (DuplicateKeyException e) {
            log.warn("У данного режиссера уже есть фильм {}", film);
        } catch (DataAccessException e) {
            throw new ValidationException("Введите правильный id режиссера");
        }
    }

}

