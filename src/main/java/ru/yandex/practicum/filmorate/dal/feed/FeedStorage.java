package ru.yandex.practicum.filmorate.dal.feed;

import ru.yandex.practicum.filmorate.enums.DbOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

public interface FeedStorage {
    void addEvent(Integer userId, EventType eventType, DbOperation operation, Integer entityId);
}
