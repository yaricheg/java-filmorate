package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreService {

    Collection<Genre> getGenres();

    Optional<Genre> getGenreById(Integer id);

    void checkGenre(Collection<Integer> genreIds);
}
