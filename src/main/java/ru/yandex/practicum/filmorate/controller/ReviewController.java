package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ReviewChecker;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody Review review) {
        log.info("Создание отзыва {}.", review);
        ReviewChecker.checkReview(review);
        return reviewService.saveReview(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        log.info("Обновеление отзыва {}.", review);
        ReviewChecker.checkReview(review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Удаление отзыва c id {}.", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info("Просмотр отзыва c id {}.", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getReviews(
            @RequestParam(value = "filmId", required = false) Integer filmId,
            @RequestParam(value = "count", defaultValue = "10") Integer count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/{type}/{userId}")
    public void addLikeOrDislike(@PathVariable Integer id,
                                 @PathVariable String type,
                                 @PathVariable Integer userId) {
        boolean isLike = parseType(type);
        log.info("Добавление {} к отзыву с id {}, от пользователя с id {}.",
                isLike ? "лайка" : "дизлайка", id, userId);
        reviewService.addLikeOrDislike(id, userId, isLike);
    }

    @DeleteMapping("/{id}/{type}/{userId}")
    public void deleteLikeOrDislike(@PathVariable Integer id,
                                    @PathVariable String type,
                                    @PathVariable Integer userId) {
        log.info("Удаление {} к отзыву с id {}, от пользователя с id {}.",
                type.equalsIgnoreCase("like") ? "лайка" : "дизлайка", id, userId);
        reviewService.deleteLikeOrDislike(id, userId);
    }

    private boolean parseType(String type) {
        if (type.equalsIgnoreCase("like")) {
            return true;
        } else if (type.equalsIgnoreCase("dislike")) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid reaction type: " + type);
        }
    }
}
