package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.Collection;


@RequiredArgsConstructor
@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        log.debug("Возвращаем всех режиссеров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Integer id) {
        log.debug("Возвращаем режиссера по id = {}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director create(@RequestBody Director director) {
        log.info("Добавляем нового режиссер {}.", director);
        return directorService.save(director);
    }

    @PutMapping
    public Director update(@RequestBody Director updateDirector) {
        log.info("Обновляем режиссера {}.", updateDirector);
        return directorService.update(updateDirector);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        directorService.delete(id);
        log.info("Удаляем директора по d = {}.", id);
    }
}
