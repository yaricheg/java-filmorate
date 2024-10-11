package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    void delete(Film film);

    Map<Integer, Film> getFilms();

}
