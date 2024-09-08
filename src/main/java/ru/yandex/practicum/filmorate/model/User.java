package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;

public @Data class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
