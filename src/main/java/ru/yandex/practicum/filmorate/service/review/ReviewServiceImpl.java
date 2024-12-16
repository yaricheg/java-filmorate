package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.feed.FeedStorage;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.enums.DbOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final FeedStorage feed;
    private final ReviewStorage reviewStorage;

    @Override
    public Review saveReview(Review review) {
        reviewStorage.checkUserById(review.getUserId());
        reviewStorage.checkFilmById(review.getFilmId());
        feed.addEvent(getReviewById(review.getReviewId()).getUserId(),
                EventType.REVIEW, DbOperation.ADD,
                review.getReviewId());
        return reviewStorage.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        reviewStorage.checkUserById(review.getUserId());
        reviewStorage.checkFilmById(review.getFilmId());
        feed.addEvent(getReviewById(review.getReviewId()).getUserId(),
                EventType.REVIEW, DbOperation.UPDATE,
                review.getReviewId());
        return reviewStorage.update(review);
    }

    @Override
    public void deleteReview(Integer id) {
        reviewStorage.delete(id);
        int userId = getReviewById(id).getUserId();
        feed.addEvent(userId, EventType.REVIEW, DbOperation.REMOVE, id);
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
    public void addLike(Integer reviewId, Integer userId) {
        reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        reviewStorage.deleteLike(reviewId, userId);
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
