package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap();

    @Override
    public Film save(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(Film updateFilm) {
        if (films.containsKey(updateFilm.getId())) {
            films.put(updateFilm.getId(), updateFilm);
            return films.get(updateFilm.getId());
        }
        throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
    }

    @Override
    public void delete(Film film) {
        films.remove(film.getId());
    }

    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }


    private Integer getNextId() {
        Integer currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Film addLike(Film film, User user) {
        Like like = new Like(film.getId(), user.getId());
        film.getLikes().add(like);
        return film;
    }

    public void deleteLike(Film film, User user) {
        List<Like> likes = film.getLikes();
        for (Like like : likes) {
            if (like.getUserId() == user.getId()) {
                likes.remove(like);
                film.setLikes(likes);
                return;
            }
        }
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        return films.values().stream()
                .filter(film -> film.getLikes() != null)
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

}
