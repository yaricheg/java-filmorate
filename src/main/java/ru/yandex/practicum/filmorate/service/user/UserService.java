package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    User addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Collection commonFriends(long userId, long otherId);

}
