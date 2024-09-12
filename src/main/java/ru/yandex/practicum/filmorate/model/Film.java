package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Film {
    private Long id;
    private String name;
    private Optional<String> description;
    private Optional<LocalDate> releaseDate;
    private Optional<Integer> duration;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = Optional.ofNullable(description);
        this.releaseDate = Optional.ofNullable(releaseDate);
        this.duration = Optional.ofNullable(duration);
    }
}
