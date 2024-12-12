package com.drewfilkins.service;

import com.drewfilkins.model.Account;
import com.drewfilkins.model.User;
import com.drewfilkins.repository.AccountRepository;
import com.drewfilkins.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {

    private final AtomicInteger userIdCounter = new AtomicInteger(0);

    private final AccountService accountService;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        accountService.setUserService(this);
    }

    public UserService(AccountService accountService, UserRepository userRepository, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public void userCreate(String login) {
        Optional<User> user = userRepository.findByLogin(login);
        if (user.isPresent()) {
            throw new IllegalArgumentException("User with login=%s already exists!%n".formatted(login));
        } else {
            User newUser = new User(userIdCounter.getAndIncrement(), login, null);
            Account account = accountService.createAccount(newUser);
            newUser.setAccountList(new ArrayList<>(Collections.singletonList(account)));
            userRepository.addUser(newUser);
            System.out.println("User created: " + newUser);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
