package ru.yandex.practicum.filmorate.enums;

import ru.yandex.practicum.filmorate.dal.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

public enum SearchFilter {
    TITLE("title"),
    DIRECTOR("director"),
    ALL("title,director");

    private String value;

    private SearchFilter(String value) {
        this.value = value;
    }

    public static SearchFilter fromString(String value) {
        if (!value.isBlank()) {
            if (value.contains(SearchFilter.TITLE.value) && value.contains(SearchFilter.DIRECTOR.value)) {
                return SearchFilter.ALL;
            } else if (value.contains(SearchFilter.TITLE.value)) {
                return SearchFilter.TITLE;
            } else if (value.contains(SearchFilter.DIRECTOR.value)) {
                return SearchFilter.DIRECTOR;
            }
        }
        throw new ValidationException("Параметр \"by\" некорректен");
    }
}
