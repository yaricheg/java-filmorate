package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film saveFilm(Film film) {
        return filmStorage.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getFilms().values();
    }

    @Override
    public void deleteFilm(Film film) {
        filmStorage.delete(film);
    }


    @Override
    public void addLike(Integer idFilm, Integer idUser) {
        if (!filmStorage.getFilms().containsKey(idFilm)) {
            throw new NotFoundException("Фильм с id = " + idFilm + " не найден");

        }
        if (!userStorage.getUsers().containsKey(idUser)) {
            throw new NotFoundException("Пользователь с id = " + idUser + " не найден");
        }
        filmStorage.getFilms().get(idFilm).addLike(idUser);
    }

    @Override
    public void deleteLike(Integer idFilm, Integer idUser) {
        if (!filmStorage.getFilms().containsKey(idFilm)) {
            throw new NotFoundException("Фильм с id = " + idFilm + " не найден");

        }
        if (!userStorage.getUsers().containsKey(idUser)) {
            throw new NotFoundException("Пользователь с id = " + idUser + " не найден");
        }
        filmStorage.getFilms().get(idFilm).deleteLike(idUser);
    }

    public Collection<Film> topFilms(Integer count) {
        if (count <= 0) {
            throw new ValidationException("Значение size должно быть больше нуля");
        }
        return filmStorage.getFilms().values().stream()
                .filter(film -> film.getLikes() != null)
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}






