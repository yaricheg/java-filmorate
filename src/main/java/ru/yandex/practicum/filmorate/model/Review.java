package ru.yandex.practicum.filmorate.model;

import lombok.*;


@Data
@EqualsAndHashCode(of = {"content", "userId", "filmId"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private int useful;
}
