package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUser();

    User createUser(User user);

    User updateUser(User updateUser);

    User addFriend(Integer userId, Integer friendId);

    User deleteFriend(Integer userId, Integer friendId);

    Collection<User> userFriends(Integer id);

    Collection commonFriends(Integer userId, Integer otherId);

    Collection<Event> getEvents(Integer userId);

}
