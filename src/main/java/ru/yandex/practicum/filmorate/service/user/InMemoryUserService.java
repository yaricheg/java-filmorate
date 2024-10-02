package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;

@Service
@AllArgsConstructor
public class InMemoryUserService implements UserService {

    private UserStorage userStorage;

    @Override
    public User addFriend(long userId, long friendId) {
        userStorage.getUserById(userId).addFriendUser(friendId);
        userStorage.getUserById(friendId).addFriendUser(userId);
        return userStorage.getUserById(userId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        userStorage.getUsers().get(userId).deleteFriendUser(friendId);
        userStorage.getUsers().get(friendId).deleteFriendUser(userId);
    }

    @Override
    public Collection commonFriends(long userId, long otherId) {
        Collection commonFriends = new HashSet<>();
        for (long idUserFriend : userStorage.getUserById(userId).getFriends()) {
            if (userStorage.getUserById(otherId).getFriends().contains(idUserFriend)) {
                commonFriends.add(idUserFriend);
            }
        }
        return commonFriends;
    }
}
