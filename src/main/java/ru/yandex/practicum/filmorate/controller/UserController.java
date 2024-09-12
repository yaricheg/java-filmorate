package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        checkUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User updateUser) throws NotFoundException {
        User oldUser;
        if (updateUser.getId() == null) {
            throw new ValidationException("Введите id");
        }
        checkUser(updateUser);
        if (users.containsKey(updateUser.getId())) {
            oldUser = users.get(updateUser.getId());
            oldUser.setEmail(updateUser.getEmail());
            log.info("Email обновлен");
            oldUser.setLogin(updateUser.getLogin());
            log.info("Логин обновлен");
            oldUser.setName(updateUser.getName());
            log.info("Имя обновлено");
            oldUser.setBirthday(updateUser.getBirthday());
            log.info("Дата Рождения обновлена");
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getName().isEmpty()) {
            user.setName(Optional.of(user.getLogin()));
        }
        if (user.getBirthday().get().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}

