package ru.yandex.practicum.filmorate.exception;


import ru.yandex.practicum.filmorate.model.Review;

public class ReviewChecker {
    public static void checkReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Отзыв не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Отзыв должен содержать тип");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("Id пользователя не может быть пустым");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("Id фильма не может быть пустым");
        }
    }
}
