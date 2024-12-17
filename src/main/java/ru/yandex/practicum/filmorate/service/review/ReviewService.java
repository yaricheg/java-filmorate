package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;


import java.util.Collection;


public interface ReviewService {


    Review saveReview(Review review);


    Review updateReview(Review review);


    void deleteReview(Integer id);


    Review getReviewById(Integer reviewId);


    Collection<Review> getReviews(Integer filmId, Integer count);


    void addLikeOrDislike(Integer reviewId, Integer userId, Boolean isLike);


    void deleteLikeOrDislike(Integer reviewId, Integer userId);
}
