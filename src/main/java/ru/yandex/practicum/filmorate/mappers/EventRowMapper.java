package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Event.builder()
                .timestamp(Instant.ofEpochSecond(rs.getInt("timestamp")))
                .userId(rs.getInt("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
