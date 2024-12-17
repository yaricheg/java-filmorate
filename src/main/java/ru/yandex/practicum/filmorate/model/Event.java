package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.enums.DbOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

@Data
@EqualsAndHashCode(of = "timestamp")
@Builder(toBuilder = true)
public class Event {
    private long timestamp;
    private Integer userId;
    private EventType eventType;
    private DbOperation operation;
    private Long eventId;
    private Integer entityId;
}
