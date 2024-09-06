package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

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
        User oldUser = new User();
        try {
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
        } catch (ValidationException e) {
            log.error("Ошибка в вводе данных пользователя. ", e);
        } catch (NotFoundException e) {
            log.error("Пользователь не найден. ", e);
        }
        return oldUser;
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
        if (user.getLogin() == null || user.getLogin().isBlank() || !(user.getLogin().contains(" "))) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null & (user.getLogin() == null
                || user.getLogin().isBlank() || !(user.getLogin().contains(" ")))) {
            throw new ValidationException("Имя для отображения может быть пустым — " +
                    "в таком случае будет использован логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}

