package ru.yandex.practicum.filmorate.storage.users;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
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
        User oldUser = users.get(updateUser.getId());
        oldUser.setEmail(updateUser.getEmail());
        log.info("Email обновлен");
        oldUser.setLogin(updateUser.getLogin());
        log.info("Логин обновлен");
        oldUser.setName(updateUser.getName());
        log.info("Имя обновлено");
        oldUser.setBirthday(updateUser.getBirthday());
        log.info("Дата Рождения обновлена");
        oldUser.setFriends(updateUser.getFriends());
        log.info("Друзья обновлены");
        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    @Override
    public Collection<User> getAll() {
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

    @Override
    public Collection<User> getUserFriends(Integer userId) {
        Set<User> friends = new HashSet<>();
        for (Integer id : users.get(userId).getFriends()) {
            friends.add(users.get(id));
        }
        return friends;
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
