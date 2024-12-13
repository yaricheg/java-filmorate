package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserChecker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.getAllUser();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        UserChecker.checkUser(user);
        userService.createUser(user);
        log.info("Пользователь добавлен {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User updateUser) {
        UserChecker.checkUser(updateUser);
        userService.updateUser(updateUser);
        log.info("Пользователь обновлен {}", updateUser);
        return updateUser;
    }

    @PutMapping("/{id}/friends/{friend_id}")
    public User addFriend(@PathVariable("id") Integer id, @PathVariable("friend_id") Integer friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") Integer id) {
        return userService.findUser(id);
    }

    @DeleteMapping("/{id}/friends/{friend_id}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable("friend_id") Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> userFriends(@PathVariable Integer id) {
        return userService.userFriends(id);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getFriendEvents(@PathVariable Integer id) {
        return userService.getEvents(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.commonFriends(id, otherId);
    }


    @GetMapping("/{id}/recommendations")
    public List<Film> getFilmRecommendationsForUser(@PathVariable Integer id) {
        return userService.getFilmRecommendationsForUser(id);
    }
}

