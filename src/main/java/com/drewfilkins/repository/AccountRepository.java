package com.drewfilkins.repository;


import com.drewfilkins.model.Account;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AccountRepository {

    List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Optional<Account> findById(int accountId) {
        return accounts.stream().filter(a -> a.getId() == accountId).findFirst();
    }

    public void deleteAccount(Account account) {
        accounts.remove(account);
    }
}
