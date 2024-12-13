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
            @RequestParam(value = "filmId", defaultValue = "-1") Integer filmId,
            @RequestParam(value = "count", defaultValue = "10") Integer count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Добавление лайка к отзыву c id {}, от пользователя с id {}.", id, userId);
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Удаление лайка к отзыву c id {}, от пользователя с id {}.", id, userId);
        reviewService.deleteLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Добавление дизлайка к отзыву c id {}, от пользователя с id {}.", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Удаление дизлайка к отзыву c id {}, от пользователя с id {}.", id, userId);
        reviewService.deleteDislike(id, userId);
    }

}
