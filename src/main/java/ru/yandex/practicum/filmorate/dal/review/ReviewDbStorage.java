package ru.yandex.practicum.filmorate.dal.review;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
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
        return getReviewById(review.getReviewId());
    }

    @Override
    public void delete(Integer id) {
        String deleteFilmSql = "DELETE FROM reviews WHERE reviews_id = ?";
        int userId = getReviewById(id).getUserId();
        jdbc.update(deleteFilmSql, id);
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
        if (filmId == null) {
            query = ALL_REVIEWS + " ORDER BY useful DESC LIMIT " + count;
        } else {
            query = "SELECT * FROM reviews WHERE film_id = " + filmId + " ORDER BY useful DESC LIMIT " + count;
        }
        return findMany(query);
    }

    @Override
    public void addLikeOrDislike(Integer reviewId, Integer userId, Boolean isLike) {
        Boolean currentType = jdbc.query(FIND_VOTE_QUERY,
                rs -> rs.next() ? rs.getBoolean("is_positive") : null, reviewId, userId);
        if (currentType == null) {
            jdbc.update("INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)",
                    reviewId, userId, isLike);
            jdbc.update("UPDATE reviews SET useful = useful + ? WHERE reviews_id = ?",
                    isLike ? 1 : -1, reviewId);
        } else if (!currentType.equals(isLike)) {
            jdbc.update("UPDATE review_likes SET is_positive = ? WHERE review_id = ? AND user_id = ?",
                    isLike, reviewId, userId);
            jdbc.update("UPDATE reviews SET useful = useful + ? WHERE reviews_id = ?",
                    isLike ? 2 : -2, reviewId);
        }
    }

    @Override
    public void deleteLikeOrDislike(Integer reviewId, Integer userId) {
        String deleteQuery = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        int rowsAffected = jdbc.update(deleteQuery, reviewId, userId);
        if (rowsAffected > 0) {
            jdbc.update("UPDATE reviews SET useful = useful - 1 WHERE reviews_id = ?", reviewId);
        }
    }
}



