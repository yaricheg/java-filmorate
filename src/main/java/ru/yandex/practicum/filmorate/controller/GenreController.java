package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getGenres() {
        log.debug("Просмотр всех жанров");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Optional<Genre> getGenresById(@PathVariable("id") Integer id) {
        log.debug("Просмотр id жанра" + id);
        return genreService.getGenreById(id);
    }
}
