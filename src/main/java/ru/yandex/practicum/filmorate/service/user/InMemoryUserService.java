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
    public Collection<User> commonFriends(Integer userId, Integer otherId) {
        Collection<User> commonFriends = new HashSet<>();
        for (Integer idUserFriend : userStorage.getUserById(userId).getFriends()) {
            if (userStorage.getUserById(otherId).getFriends().contains(idUserFriend)) {
                commonFriends.add(userStorage.getUserById(idUserFriend));
            }
        }

        return commonFriends;
    }
}
