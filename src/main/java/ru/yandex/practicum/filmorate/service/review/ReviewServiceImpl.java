package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.users.UserStorage;
import ru.yandex.practicum.filmorate.dal.feed.FeedStorage;
import ru.yandex.practicum.filmorate.enums.DbOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final FeedStorage feed;
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Review saveReview(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());
        Review savedReview = reviewStorage.save(review);
        feed.addEvent(getReviewById(review.getReviewId()).getUserId(),
                EventType.REVIEW, DbOperation.ADD,
                review.getReviewId());
        return savedReview;
    }

    @Override
    public Review updateReview(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());
        feed.addEvent(getReviewById(review.getReviewId()).getUserId(),
                EventType.REVIEW, DbOperation.UPDATE,
                review.getReviewId());
        return reviewStorage.update(review);
    }

    @Override
    public void deleteReview(Integer id) {
        int userId = getReviewById(id).getUserId();
        feed.addEvent(userId, EventType.REVIEW, DbOperation.REMOVE, id);
        reviewStorage.delete(id);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    @Override
    public Collection<Review> getReviews(Integer filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @Override
    public void addLikeOrDislike(Integer reviewId, Integer userId, Boolean isLike) {
        userStorage.getUserById(userId);
        reviewStorage.addLikeOrDislike(reviewId, userId, isLike);
    }

    @Override
    public void deleteLikeOrDislike(Integer reviewId, Integer userId) {
        userStorage.getUserById(userId);
        reviewStorage.deleteLikeOrDislike(reviewId, userId);
    }
}

