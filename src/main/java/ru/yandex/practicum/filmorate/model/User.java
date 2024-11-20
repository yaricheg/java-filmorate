package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;

    /*public void addFriendUser(Integer id) {
        friends.add(id);
    }

    public void deleteFriendUser(Integer id) {
        friends.remove(id);
    }*/

   /* public Set<Integer> setFriends(Set<Integer> newFriends) {
        this.friends = newFriends;
        return friends;
    }*/

}
