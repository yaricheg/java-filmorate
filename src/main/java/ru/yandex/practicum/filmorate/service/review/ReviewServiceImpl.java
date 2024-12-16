package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.users.UserStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {


    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Override
    public Review saveReview(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());
        return reviewStorage.save(review);
    }


    @Override
    public Review updateReview(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());
        return reviewStorage.update(review);
    }


    @Override
    public void deleteReview(Integer id) {
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

