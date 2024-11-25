package ru.yandex.practicum.filmorate.dal.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        final String addFriendSql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        try {
            int rowsAffected = jdbc.update(addFriendSql, userId, friendId);
            if (rowsAffected == 1) {
                log.info("Заявка в друзья отправлена");
            } else {
                log.warn("Не удалось отправить отправить заявку в друзья");
            }

        } catch (RuntimeException e) {
            log.info("Ошибка при добавлении в друзья между пользователями {} и {}", userId, friendId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении заявки в друзья", e.getMessage());
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        final String deleteFriendSql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        try {
            if (checkLink(userId, friendId)) {
                jdbc.update(deleteFriendSql, userId, friendId);
            } else {
                throw new NotFoundException("Пользователи " + userId + " и " + friendId
                        + " не друзья");
            }
        } catch (Exception e) {
            log.error("Ошибка при удаление из друзей");
        }
    }

    private boolean checkLink(Integer userId, Integer friendId) {
        String checkLinkSql = "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
        int count = jdbc.queryForObject(checkLinkSql, Integer.class, userId, friendId);
        return count > 0;
    }
}