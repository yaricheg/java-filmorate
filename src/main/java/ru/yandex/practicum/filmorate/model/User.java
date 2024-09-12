package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String email;
    private String login;
    private Optional<String> name;
    private Optional<LocalDate> birthday;


    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = Optional.ofNullable(name);
        this.birthday = Optional.ofNullable(birthday);
    }
}
