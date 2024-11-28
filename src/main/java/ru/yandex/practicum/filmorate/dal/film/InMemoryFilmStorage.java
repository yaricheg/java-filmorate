package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Deprecated
    @Override
    public void addLike(Integer filmId, Integer userId) {

    }

    @Deprecated
    @Override
    public void deleteLike(Integer filmId, Integer userId) {

    }


    private Integer getNextId() {
        Integer currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
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

    @Deprecated
    @Override
    public Mpa getMpaById(Integer mpaId) {
        return null;
    }


    @Deprecated
    @Override
    public User getUserById(Integer userId) {
        return null;
    }

    @Deprecated
    @Override
    public int[] batchUpdateAddGenre(List<Integer> genres, Integer filmId) {
        return new int[0];
    }

    //@Deprecated
    //@Override
   // public void addFilmGenre(Integer filmId, Integer genreId){}

    @Deprecated
    @Override
    public Collection<Genre> getAllFilmGenresByFilmId(Integer filmId) {
        return null;
    }

   /* @Deprecated
    @Override
    public void deleteAllFilmGenresByFilmId(Integer filmId) {

    }*/

    @Deprecated
    @Override
    public Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films) {
        return null;
    }

    @Deprecated
    @Override
    public List<Like> getLikesFilmId(Integer filmId) {
        return null;
    }

}
