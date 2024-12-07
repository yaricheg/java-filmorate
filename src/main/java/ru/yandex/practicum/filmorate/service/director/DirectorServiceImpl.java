package ru.yandex.practicum.filmorate.service.director;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.director.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorServiceImpl(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }


    @Override
    public Director save(Director director) {
        return directorStorage.save(director);
    }

    @Override
    public Director update(Director director) {
        directorStorage.getDirectorById(director.getId());
        return directorStorage.update(director);
    }

    @Override
    public void delete(Integer id) {
        directorStorage.delete(id);
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        return directorStorage.getDirectorById(directorId);
    }

    @Override
    public Collection<Director> getDirectors() {
        return directorStorage.getDirectors();
    }
}
