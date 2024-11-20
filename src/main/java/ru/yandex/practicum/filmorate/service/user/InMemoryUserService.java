package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.users.UserStorage;

import java.util.*;

@Service
//@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    public InMemoryUserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
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
    public User updateUser(User updateUser) {

        return userStorage.update(updateUser);
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        userStorage.addFriend(userId, friendId);
        return user;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    @Override
    public Collection<User> userFriends(Integer id) {
        userStorage.getUserById(id);
        return userStorage.userFriends(id);
    }

    @Override
    public Collection<User> commonFriends(Integer userId, Integer otherId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(otherId);
        return userStorage.commonFriends(user.getId(), friend.getId());
    }
}
