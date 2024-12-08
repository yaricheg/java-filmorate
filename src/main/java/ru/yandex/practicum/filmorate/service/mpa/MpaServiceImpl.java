package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    public Mpa getMpaById(Integer id) {
        Optional<Mpa> mpa = mpaStorage.getMpaById(id);
        if (mpa == null || mpa.isEmpty()) {
            throw new NotFoundException(" Введите правильный id рейтинга");
        }
        return mpa.get();

    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}