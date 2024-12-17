package ru.yandex.practicum.filmorate.dal.review;

import ru.yandex.practicum.filmorate.model.Review;


import java.util.Collection;


public interface ReviewStorage {


    Review save(Review review);


    Review update(Review review);


    void delete(Integer id);


    Review getReviewById(Integer reviewId);


    Collection<Review> getReviews(Integer filmId, Integer count);


    void addLikeOrDislike(Integer reviewId, Integer userId, Boolean isLike);


    void deleteLikeOrDislike(Integer reviewId, Integer userId);
}
