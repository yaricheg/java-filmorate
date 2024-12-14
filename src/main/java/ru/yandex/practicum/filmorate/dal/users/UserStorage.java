package ru.yandex.practicum.filmorate.dal.users;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<Event> getEvents(Integer userId);

    Collection<User> getAll();

    User create(User user);

    User update(User updateUser);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> userFriends(Integer id);

    Collection commonFriends(Integer userId, Integer otherId);

    void deleteUser(Integer id);

    User getUserById(Integer id);

    List<Film> getFilmRecommendationsForUser(int userId);
}
