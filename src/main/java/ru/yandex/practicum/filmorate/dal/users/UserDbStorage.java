package ru.yandex.practicum.filmorate.dal.users;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String ALL_USERS = "SELECT * FROM users";

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
            return null;
        }
    }

    @Override
    public void deleteUser(User user) {
        final String deleteUserSql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteUserSql, user.getId());
    }


    @Override
    public void addFriend(Integer userId, Integer friendId) {
        final String addFriendSql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(addFriendSql, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        final String deleteFriendSql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteFriendSql, userId, friendId);

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
    public Collection commonFriends(Integer userId, Integer otherId) {
        final String sql = "SELECT * FROM users WHERE id IN " +
                "(SELECT friend_id " +
                "FROM users AS u " +
                "JOIN friendship AS f ON u.id = f.user_id WHERE u.id = ?) " +
                "AND id IN (SELECT friend_id FROM users AS u " +
                "JOIN friendship f ON u.id = f.user_id WHERE u.id = ?)";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId, otherId);
    }

}