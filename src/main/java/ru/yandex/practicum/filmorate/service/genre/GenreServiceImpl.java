package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDbStorage genreDbStorage;

    @Override
    public Collection<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return genreDbStorage.getGenreById(id);
    }

    @Override
    public void checkGenre(Collection<Integer> genreIds) {
        Collection<Optional<Genre>> genres = genreIds.stream()
                .map(genreId -> genreDbStorage.getGenreById(genreId)).collect(Collectors.toList());
        if (!genreDbStorage.getGenres().retainAll(genres)) {
            throw new ValidationException("ошибка в id жанра");
        }
    }


}
