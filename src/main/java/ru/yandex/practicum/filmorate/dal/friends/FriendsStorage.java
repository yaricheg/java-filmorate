package ru.yandex.practicum.filmorate.dal.friends;

public interface FriendsStorage {
    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);
}