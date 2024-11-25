package ru.yandex.practicum.filmorate.dal.users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User updateUser);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> userFriends(Integer id);

    Collection commonFriends(Integer userId, Integer otherId);

    void deleteUser(User user);

    User getUserById(Integer id);
}
