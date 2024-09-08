package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

public @Data class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}
