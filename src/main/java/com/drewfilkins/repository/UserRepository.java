package com.drewfilkins.repository;

import com.drewfilkins.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {
    List<User> users = new ArrayList<>();

    public Optional<User> findByLogin(String login) {
        return users.stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }

    public Optional<User> findById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    public List<User> findAll() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }
}
