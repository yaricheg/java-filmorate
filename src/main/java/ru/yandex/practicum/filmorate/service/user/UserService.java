package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> getAllUser();

    User createUser(User user);

    void deleteUser(Integer id);

    User findUser(Integer id);

    User updateUser(User updateUser);

    User addFriend(Integer userId, Integer friendId);

    User deleteFriend(Integer userId, Integer friendId);

    Collection<User> userFriends(Integer id);

    Collection commonFriends(Integer userId, Integer otherId);

    List<Film> getFilmRecommendationsForUser(Integer userId);

    Collection<Event> getEvents(Integer userId);

}
