package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {

    Director save(Director director);

    Director update(Director director);

    void delete(Integer id);

    Director getDirectorById(Integer directorId);

    Collection<Director> getDirectors();
}
