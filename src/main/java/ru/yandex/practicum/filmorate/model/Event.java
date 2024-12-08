package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Event {
    long timestamp;
    Integer userId;
    String eventType;
    String operation;
    Long eventId;
    Integer entityId;
}
