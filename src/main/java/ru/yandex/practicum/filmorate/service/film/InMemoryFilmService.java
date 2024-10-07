package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InMemoryFilmService implements FilmService {
    private FilmStorage filmStorage;

    @Override
    public void addLike(Integer idFilm, Integer idUser) {
        filmStorage.getFilms().get(idFilm).addLike(idUser);
    }

    @Override
    public void deleteLike(Integer idFilm, Integer idUser) {
        filmStorage.getFilms().get(idFilm).deleteLike(idUser);
    }

    public Collection<Film> topFilms(Integer count) {
        return filmStorage.getFilms().values().stream()
                .filter(film -> film.getLikes() != null)
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}






