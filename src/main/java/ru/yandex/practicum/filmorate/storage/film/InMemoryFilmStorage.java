package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film save(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(Film updateFilm) throws ValidationException, NotFoundException {
        Film oldFilm;
        if (films.containsKey(updateFilm.getId())) {
            oldFilm = films.get(updateFilm.getId());
            if (!(updateFilm.getDescription() == null || updateFilm.getDescription().isBlank())) {
                oldFilm.setDescription(updateFilm.getDescription());
                log.info("Описание обновлено");
            }
            if (!(updateFilm.getReleaseDate() == null)) {
                oldFilm.setReleaseDate(updateFilm.getReleaseDate());
                log.info("Дата обновлена");
            }
            if (!(updateFilm.getDuration() == null)) {
                oldFilm.setDuration(updateFilm.getDuration());
                log.info("Продолжительность фильма обновлена");
            }
            oldFilm.setName(updateFilm.getName());
            log.info("Название фильма обновлено");
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
    }

    public void deleteFilm(Film film) {
        films.remove(film.getId());
    }

    public Map<Integer, Film> getFilms() {
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
