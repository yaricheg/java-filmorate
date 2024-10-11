package ru.yandex.practicum.filmorate.storage.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final CheckUser checkUser = new CheckUser();

    @Override
    public User create(User user) {
        if (user == null) {
            throw new NullPointerException("Пользователь равен null");
        }
        checkUser.checkUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User updateUser) {
        if (updateUser == null) {
            throw new NullPointerException("Обновленный пользователь равен null");
        }
        if (getUsers().containsKey(updateUser.getId())) {
            checkUser.checkUser(updateUser);
            users.put(updateUser.getId(), updateUser);
            return users.get(updateUser.getId());
        }
        throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");

    }

    @Override
    public Collection<User> getAll() {
        if (users.values().contains(null)) {
            throw new NullPointerException("Cписок пользователей содержит null");
        }
        return users.values();
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }


    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }


    private Integer getNextId() {
        Integer currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
