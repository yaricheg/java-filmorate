package ru.yandex.practicum.filmorate.dal.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review save(Review review);

    Review update(Review review);

    void delete(Integer id);

    Review getReviewById(Integer reviewId);

    Collection<Review> getReviews(Integer filmId, Integer count);

    void addLike(Integer reviewId, Integer userId);

    void deleteLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);

    void deleteDislike(Integer reviewId, Integer userId);

    void checkUserById(Integer userId);

    void checkFilmById(Integer id);
}
