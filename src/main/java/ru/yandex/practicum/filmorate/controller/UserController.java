package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;


@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {
    private UserStorage userStorage;
    private UserService userService;


    @GetMapping
    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        checkNullUser(user);
        checkUser(user);
        userStorage.create(user);
        log.info("Пользователь добавлен {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User updateUser) {
        checkNullUser(updateUser);
        checkUser(updateUser);
        if (userStorage.getUsers().containsKey(updateUser.getId())) {
            userStorage.update(updateUser);
            return userStorage.getUsers().get(updateUser.getId());
        }
        throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");
    }

    @PutMapping("/{id}/friends/{friend_id}")
    public User addFriend(@PathVariable Long id, @PathVariable("friend_id") Long friendId) {
        if (!(userStorage.getUsers().containsKey(id)) ||
                !(userStorage.getUsers().containsKey(friendId))) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friend_id}")
    public void deleteFriend(@PathVariable Long id, @PathVariable("friend_id") Long friendId) {
        if (!(userStorage.getUsers().containsKey(id)) ||
                !(userStorage.getUsers().containsKey(friendId))) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection userFriends(@PathVariable long id) {
        if (!(userStorage.getUsers().containsKey(id))) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return userStorage.getUsers().get(id).getFriends();
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection findCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.commonFriends(id, otherId);
    }

    private void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void checkNullUser(User user) {
        if (user.getEmail() == null) {
            throw new NullPointerException("Значение null в поле электронной почты");
        }
        if (user.getLogin() == null) {
            throw new NullPointerException("Значение null в поле логина");
        }
        if (user.getBirthday() == null) {
            throw new NullPointerException("Значение null в поле даты рождения");
        }
    }
}

