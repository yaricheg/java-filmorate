package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Film save(Film film);

    Collection<Film> getAll();

    Film update(Film film);

    void deleteFilm(Film film);

    Map<Long, Film> getFilms();


}
