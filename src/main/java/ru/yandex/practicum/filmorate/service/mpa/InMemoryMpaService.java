package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InMemoryMpaService implements MpaService {

    private final MpaStorage mpaStorage;

    public Mpa getMpaById(Integer id) {
        Optional<Mpa> mpa = mpaStorage.getMpaById(id);
        if (mpa.isEmpty()) {
            throw new ValidationException("Mpa с id " + id + " не найден");
        } else {
            return mpa.get();
        }
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}