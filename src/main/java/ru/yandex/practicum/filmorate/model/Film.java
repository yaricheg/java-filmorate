package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private final static Logger log = LoggerFactory.getLogger(Film.class);

}
