package ru.yandex.practicum.filmorate.dal.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.LikeMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbc;

    @Override
    public void addLike(Integer filmId, Integer userId) {
        final String insertQuery = "INSERT INTO likes (film_id, user_id) values (?, ?)";
        final String increaseRateQuery = "UPDATE films SET rate = rate + 1 WHERE id = ?";
        try {
            jdbc.update(insertQuery, filmId, userId);
            jdbc.update(increaseRateQuery, filmId);
        } catch (Exception e) {
            log.error("Error while adding like", e);
        }
    }

    @Override
    public boolean removeLike(Integer filmId, Integer userId) {
        final String deleteQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        final String decreaseRateQuery = "UPDATE films SET rate = rate + 1 WHERE id = ?";
        return ((jdbc.update(deleteQuery, filmId, userId) > 0) && (jdbc.update(decreaseRateQuery, filmId) > 0));
    }

    @Override
    public List<Like> getLikesFilmId(Integer filmId) {
        final String getLikesByFilmId = "SELECT * FROM likes WHERE film_id = ?";
        return jdbc.query(getLikesByFilmId, new LikeMapper(), filmId);
    }
}