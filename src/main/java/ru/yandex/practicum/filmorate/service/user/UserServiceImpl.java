package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.users.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> getAllUser() {
        return userStorage.getAll();
    }

    @Override
    public User createUser(User user) {
        return userStorage.create(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }

    @Override
    public User updateUser(User updateUser) {

        return userStorage.update(updateUser);
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        userStorage.addFriend(userId, friendId);
        return user;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        userStorage.deleteFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    @Override
    public Collection<User> userFriends(Integer id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        userStorage.getUserById(id);
        return userStorage.userFriends(id);
    }

    @Override
    public Collection<User> commonFriends(Integer userId, Integer otherId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(otherId);
        return userStorage.commonFriends(user.getId(), friend.getId());
    }

    @Override
    public Collection<Event> getEvents(Integer userId) {
        return userStorage.getEvents(userId);
    }

}
