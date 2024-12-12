package ru.yandex.practicum.filmorate.dal.review;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.Instant;
import java.util.Collection;

@Repository("reviewDbStorage")
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String FIND_VOTE_QUERY = "SELECT is_positive FROM review_likes " +
            "WHERE review_id = ? AND user_id = ?";

    private static final String ALL_REVIEWS = "SELECT * FROM reviews";

    private static final String INSERT_QUERY = "INSERT INTO reviews (content, type, user_id, film_id) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, type = ? WHERE reviews_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review save(Review review) {
        Integer id = Math.toIntExact(insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        ));
        review.setReviewId(id);
        addEvent(review.getUserId(), "ADD", review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review update(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        addEvent(review.getUserId(), "UPDATE", review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void delete(Integer id) {
        String deleteFilmSql = "DELETE FROM reviews WHERE reviews_id = ?";
        int userId = getReviewById(id).getUserId();
        jdbc.update(deleteFilmSql, id);
        addEvent(userId, "REMOVE", id);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        try {
            return jdbc.queryForObject(ALL_REVIEWS.concat(" WHERE reviews_id = ?"),
                    new ReviewRowMapper(),
                    reviewId);
        } catch (DataAccessException e) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }
    }

    @Override
    public Collection<Review> getReviews(Integer filmId, Integer count) {
        String query;
        if (filmId == -1) {
            query = ALL_REVIEWS + " ORDER BY useful DESC LIMIT " + count;
        } else {
            query = "SELECT * FROM reviews WHERE film_id = " + filmId + " ORDER BY useful DESC LIMIT " + count;
        }
        return findMany(query);
    }

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        checkUserById(userId);
        Boolean currentLike = jdbc.query(FIND_VOTE_QUERY,
                rs -> rs.next() ? rs.getBoolean("is_positive") : null, reviewId, userId);
        if (currentLike == null) {
            jdbc.update("INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, TRUE)",
                    reviewId, userId);
            jdbc.update("UPDATE reviews SET useful = useful + 1 WHERE reviews_id = ?", reviewId);
        } else if (!currentLike) {
            jdbc.update("UPDATE review_likes SET is_positive = TRUE WHERE review_id = ? AND user_id = ?",
                    reviewId, userId);
            jdbc.update("UPDATE reviews SET useful = useful + 2 WHERE reviews_id = ?", reviewId);
        }
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        checkUserById(userId);
        Boolean currentLike = jdbc.query(FIND_VOTE_QUERY,
                rs -> rs.next() ? rs.getBoolean("is_positive") : null, reviewId, userId);
        if (currentLike == null) {
            jdbc.update("INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, FALSE)",
                    reviewId, userId);
            jdbc.update("UPDATE reviews SET useful = useful - 1 WHERE reviews_id = ?", reviewId);
        } else if (currentLike) {
            jdbc.update("UPDATE review_likes SET is_positive = FALSE WHERE review_id = ? AND user_id = ?",
                    reviewId, userId);
            jdbc.update("UPDATE reviews SET useful = useful - 2 WHERE reviews_id = ?", reviewId);
        }
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        checkUserById(userId);
        String deleteQuery = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = TRUE";
        int rowsAffected = jdbc.update(deleteQuery, reviewId, userId);
        if (rowsAffected > 0) {
            jdbc.update("UPDATE reviews SET useful = useful - 1 WHERE reviews_id = ?", reviewId);
        }
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        checkUserById(userId);
        String deleteQuery = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = FALSE";
        int rowsAffected = jdbc.update(deleteQuery, reviewId, userId);
        if (rowsAffected > 0) {
            jdbc.update("UPDATE reviews SET useful = useful + 1 WHERE reviews_id = ?", reviewId);
        }
    }

    @Override
    public void checkFilmById(Integer filmId) {
        String checkUserSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        int userCount = jdbc.queryForObject(checkUserSql, Integer.class, filmId);
        if (userCount == 0) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    public void checkUserById(Integer userId) {
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        int userCount = jdbc.queryForObject(checkUserSql, Integer.class, userId);
        if (userCount == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private void addEvent(Integer userId, String operation, Integer entityId) {
        String sql = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";

        long timestamp = Instant.now().toEpochMilli();

        Event event = Event.builder()
                .timestamp(timestamp)
                .userId(userId)
                .eventType("REVIEW")
                .operation(operation)
                .entityId(entityId)
                .build();

        jdbc.update(sql, timestamp, userId, "REVIEW", operation, entityId);
    }
}


