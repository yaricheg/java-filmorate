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

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User updateUser) {
        if (updateUser == null) {
            throw new NullPointerException("Обновленный пользователь равен null");
        }
        if (users.containsKey(updateUser.getId())) {
            users.put(updateUser.getId(), updateUser);
            return users.get(updateUser.getId());
        }
        throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");

    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
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
