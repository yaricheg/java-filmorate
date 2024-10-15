package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;

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
        friend.getFriends().add(userId);
        user.getFriends().add(friendId);
        return user;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return friend;
    }

    @Override
    public Collection<User> userFriends(Integer id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(friend -> userStorage.getUserById(friend))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> commonFriends(Integer userId, Integer otherId) {
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Integer> friendFriends = userStorage.getUserById(otherId).getFriends();
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
                .map(user -> userStorage.getUserById(user))
                .collect(Collectors.toList());
    }
}
