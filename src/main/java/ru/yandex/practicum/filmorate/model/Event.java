package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
public class Event {
    Instant timestamp;
    Integer userId;
    String eventType;// одно из значениий LIKE, REVIEW или FRIEND
    String operation;// одно из значениий REMOVE, ADD, UPDATE
    Long eventId;//primary key
    Integer entityId; // идентификатор сущности, с которой произошло событие
}
