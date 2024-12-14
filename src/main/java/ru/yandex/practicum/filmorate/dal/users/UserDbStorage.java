package ru.yandex.practicum.filmorate.dal.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.UserNotFound;
import ru.yandex.practicum.filmorate.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private static final String ALL_USERS = "SELECT * FROM users";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        final String createUserSql = "INSERT INTO users (name, login, birthday, email) VALUES (?, ?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    createUserSql,
                    new String[]{"id"}
            );
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setObject(3, user.getBirthday());
            preparedStatement.setString(4, user.getEmail());
            return preparedStatement;
        }, generatedKeyHolder);
        int userId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        if (getUserById(user.getId()) != null) {
            final String updateUserSql = "UPDATE users SET name = ?, login = ?, birthday = ?, email = ? where id = ?";
            jdbcTemplate.update(
                    updateUserSql,
                    user.getName(), user.getLogin(), user.getBirthday(), user.getEmail(), user.getId()
            );
            return getUserById(user.getId());
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(ALL_USERS, new UserRowMapper());
    }

    @Override
    public User getUserById(Integer userId) {
        try {
            return jdbcTemplate.queryForObject(ALL_USERS.concat(" WHERE id = ?"), new UserRowMapper(), userId);
        } catch (DataAccessException e) {
            throw new UserNotFound("USER NOT FOUND");
        }
    }

    @Override
    public void deleteUser(Integer id) {
        final String deleteUserSql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(deleteUserSql, id);

        if (rowsAffected == 0) {
            throw new UserNotFound("Пользователь с ID " + id + " не найден.");
        }
    }


    @Override
    public void addFriend(Integer userId, Integer friendId) {
        final String addFriendSql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(addFriendSql, userId, friendId);

        addEvent(userId, "ADD", friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        final String deleteFriendSql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteFriendSql, userId, friendId);

        addEvent(userId, "REMOVE", friendId);
    }

    @Override
    public Collection<User> userFriends(Integer id) {
        final String sql = "SELECT * " +
                "FROM users " +
                "WHERE id IN (SELECT f.friend_id " +
                "FROM users AS u " +
                "JOIN friendship AS f ON u.id = f.user_id WHERE u.id = ?)";
        return jdbcTemplate.query(sql, new UserRowMapper(), id);
    }

    @Override
    public Collection<Event> getEvents(Integer userId) {
        log.info("Просматривают feed");
        String sql = "SELECT * FROM events WHERE user_id = ?";
//        String sql = "SELECT e.* " +
//                "FROM events e " +
//                "INNER JOIN friendship f ON e.user_id = f.friend_id " +
//                "WHERE f.user_id = ? " +
//                "UNION " +
//                "SELECT e.* " +
//                "FROM events e " +
//                "WHERE e.user_id = ? " +
//                "ORDER BY timestamp ASC; ";
        getUserById(userId);
        return jdbcTemplate.query(sql, new EventRowMapper(), userId);//, userId
    }

    @Override
    public Collection commonFriends(Integer userId, Integer otherId) {
        final String sql = "SELECT * FROM users WHERE id IN " +
                "(SELECT friend_id " +
                "FROM users AS u " +
                "JOIN friendship AS f ON u.id = f.user_id WHERE u.id = ?) " +
                "AND id IN (SELECT friend_id FROM users AS u " +
                "JOIN friendship f ON u.id = f.user_id WHERE u.id = ?)";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId, otherId);
    }

    @Override
    public List<Film> getFilmRecommendationsForUser(int userId) {
        String sql = "SELECT DISTINCT f.id AS film_id, " +
                "                f.name AS film_name, " +
                "                f.description AS description, " +
                "                f.release_date AS release_date, " +
                "                f.duration AS duration, " +
                "                f.rate AS rate, " +
                "                m.id AS mpa_id, " +
                "                m.name AS mpa_name " +
                "FROM likes fl2 " +
                "JOIN ( " +
                "    SELECT fl1.user_id AS common_user_id, " +
                "           COUNT(*) AS common_likes_cnt " +
                "    FROM likes fl1 " +
                "    JOIN ( " +
                "        SELECT DISTINCT film_id " +
                "        FROM likes " +
                "        WHERE user_id = ? " +
                "    ) AS ul ON fl1.film_id = ul.film_id " +
                "    WHERE fl1.user_id != ? " +
                "    GROUP BY fl1.user_id " +
                "    HAVING COUNT(*) = ( " +
                "        SELECT MAX(common_likes_cnt) " +
                "        FROM ( " +
                "            SELECT COUNT(*) AS common_likes_cnt " +
                "            FROM likes fl1 " +
                "            JOIN ( " +
                "                SELECT DISTINCT film_id " +
                "                FROM likes " +
                "                WHERE user_id = ? " +
                "            ) AS ul ON fl1.film_id = ul.film_id " +
                "            WHERE fl1.user_id != ? " +
                "            GROUP BY fl1.user_id " +
                "        ) AS common_likes " +
                "    ) " +
                ") AS tcu ON fl2.user_id = tcu.common_user_id " +
                "JOIN films f ON fl2.film_id = f.id " +
                "JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE fl2.film_id NOT IN ( " +
                "    SELECT film_id " +
                "    FROM likes " +
                "    WHERE user_id = ? " +
                ")";
        return jdbcTemplate.query(sql, new FilmRowMapper(), userId, userId, userId, userId, userId);
    }

    private void addEvent(Integer userId, String operation, Integer entityId) {
        String sql = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";
        long timestamp = Instant.now().toEpochMilli();

        jdbcTemplate.update(sql, timestamp, userId, "FRIEND", operation, entityId);
    }

}