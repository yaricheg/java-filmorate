package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dal.BaseRepository;

import java.util.Collection;
import java.util.Optional;

@Component
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String ALL_GENRES = "SELECT * FROM genres";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Genre> getGenreById(Integer genreId) {
        return findOne(ALL_GENRES.concat(" WHERE id = ?"), genreId);
    }

    public Collection<Genre> getGenres() {
        return findMany(ALL_GENRES.concat(" ORDER BY id ASC"));
    }
}