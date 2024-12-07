package ru.yandex.practicum.filmorate.dal.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {

    Director save(Director director);

    Director update(Director director);

    void delete(Integer id);

    Director getDirectorById(Integer directorId);

    Collection<Director> getDirectors();
}
