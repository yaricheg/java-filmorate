package ru.yandex.practicum.filmorate.storage.users;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User updateUser);

    Collection<User> getAll();

    void deleteUser(User user);

    Map<Integer, User> getUsers();

    User getUserById(Integer id);
}
