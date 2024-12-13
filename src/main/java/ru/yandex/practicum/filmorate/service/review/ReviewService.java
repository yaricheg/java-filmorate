package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {

    Review saveReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Integer id);

    Review getReviewById(Integer reviewId);

    Collection<Review> getReviews(Integer filmId, Integer count);

    void addLike(Integer reviewId, Integer userId);

    void deleteLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);

    void deleteDislike(Integer reviewId, Integer userId);
}
