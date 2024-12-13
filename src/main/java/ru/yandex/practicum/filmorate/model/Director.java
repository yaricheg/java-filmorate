package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.containSpaces.ContainSpaces;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    private Integer id;
    @ContainSpaces
    private String name;
}
