package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;

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
