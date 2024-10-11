package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.CheckFilm;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final CheckFilm checkFilm = new CheckFilm();

    private final HashMap<Integer, Film> films = new HashMap();

    @Override
    public Film save(Film film) {
        if (film == null) {
            throw new NullPointerException("Фильм равен null");
        }
        checkFilm.checkFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(Film updateFilm) throws ValidationException, NotFoundException {
        if (updateFilm == null) {
            throw new NullPointerException("Обновленный фильм равен null");
        }
        if (films.containsKey(updateFilm.getId())) {
            checkFilm.checkFilm(updateFilm);
            films.put(updateFilm.getId(), updateFilm);
            return films.get(updateFilm.getId());
        }
        throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
    }


    @Override
    public void delete(Film film) {
        films.remove(film.getId());
    }

    public Map<Integer, Film> getFilms() {
        if (films.values().contains(null)) {
            throw new NullPointerException("Список фильмов содержит null");
        }
        return films;
    }

    private Integer getNextId() {
        Integer currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
