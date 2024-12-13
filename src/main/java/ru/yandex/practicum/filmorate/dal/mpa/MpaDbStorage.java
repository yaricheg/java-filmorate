package ru.yandex.practicum.filmorate.dal.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Component
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String MPA_QUERY = "SELECT * FROM mpa";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Mpa> getMpaById(Integer mpaId) {
        Optional<Mpa> mpa = findOne(MPA_QUERY.concat(" WHERE id = ?"), mpaId);
        if (mpa == null || mpa.isEmpty()) {
            throw new NotFoundException("Возрастной рейтинг с id " + mpaId + " не найден");
        }
        return mpa;
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return findMany(MPA_QUERY.concat(" ORDER BY id ASC"));
    }
}