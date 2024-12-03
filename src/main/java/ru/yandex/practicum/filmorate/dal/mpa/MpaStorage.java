package ru.yandex.practicum.filmorate.dal.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Optional<Mpa> getMpaById(Integer mpaId);

    Collection<Mpa> getAllMpa();
}