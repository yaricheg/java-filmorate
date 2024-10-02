package ru.yandex.practicum.filmorate.storage.users;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

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
        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
