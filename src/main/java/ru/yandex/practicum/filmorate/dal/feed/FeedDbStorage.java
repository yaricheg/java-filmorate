package ru.yandex.practicum.filmorate.dal.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.DbOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbc;

    @Override
    public void addEvent(Integer userId, EventType eventType, DbOperation operation, Integer entityId) {
        String sql = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";

        long timestamp = Instant.now().toEpochMilli();
        jdbc.update(sql, timestamp, userId, eventType.toString(), operation.toString(), entityId);
    }
}
