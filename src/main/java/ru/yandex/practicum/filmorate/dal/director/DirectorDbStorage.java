package ru.yandex.practicum.filmorate.dal.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Repository("directorDbStorage")
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {

    private static final String GET_ALL_QUERY = "SELECT * FROM directors";

    private static final String GET_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";

    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";

    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Director save(Director director) {
        Integer id = Math.toIntExact(insert(
                INSERT_QUERY,
                director.getName()
        ));
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    @Override
    public void delete(Integer id) {
        delete(
                DELETE_QUERY,
                id
        );
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        Optional<Director> mayBeDirector = findOne(GET_BY_ID_QUERY, directorId);
        if (mayBeDirector.isEmpty()) {
            throw new NotFoundException("Режиссер под id = " + directorId + " не найден");
        }
        return mayBeDirector.get();
    }

    @Override
    public Collection<Director> getDirectors() {
        return findMany(GET_ALL_QUERY);
    }
}
