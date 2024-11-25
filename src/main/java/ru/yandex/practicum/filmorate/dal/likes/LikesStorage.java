package ru.yandex.practicum.filmorate.dal.likes;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikesStorage {
    void addLike(Integer filmId, Integer userId);

    boolean removeLike(Integer filmId, Integer userId);

    List<Like> getLikesFilmId(Integer filmId);
}