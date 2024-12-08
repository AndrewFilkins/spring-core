package com.drewfilkins.service;

import com.drewfilkins.model.Account;
import com.drewfilkins.model.User;
import com.drewfilkins.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountService {

    private final AtomicInteger accountIdCounter = new AtomicInteger(0);

    UserService userService;
    AccountRepository accountRepository;

    @Value("${account.default-amount}")
    private Integer defaultMoneyAmount;

    @Value("${account.transfer-commission}")
    private Integer transferCommission;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Account createAccount(User user) {
        Account account = new Account(accountIdCounter.getAndIncrement(), user.getId(), defaultMoneyAmount);
        accountRepository.addAccount(account);
        return account;
    }

    public Account createAccountByUserId(int userId) {
        Optional<User> user = userService.getUserById(userId);
        Account account = null;
        if (user.isPresent()) {
            User currUser = user.get();
            account = createAccount(currUser);
            currUser.getAccountList().add(account);
            System.out.printf("New account created with ID: %d for user: %s%n", account.getId(), user.get().getLogin());
            accountRepository.addAccount(account);
        } else {
            System.out.println("There's no user with such id!");
        }
        return account;
    }

    public void accountDeposit(int accountId, int amount) {
        if (amount > 0) {
            Optional<Account> account = accountRepository.findById(accountId);
            if (account.isPresent()) {
                Account currAccount = account.get();
                currAccount.setMoneyAmount(currAccount.getMoneyAmount() + amount);
                System.out.printf("Amount %d deposited to account ID: %d%n", amount, accountId);
            } else {
                System.out.println("There's no account with such id!");
            }
        } else {
            System.out.println("Amount should be greater than zero!");
        }
    }

    public void accountWithdraw(int accountId, int amountToWithdraw) {
        if (amountToWithdraw > 0) {
            Optional<Account> account = accountRepository.findById(accountId);
            if (account.isPresent()) {
                Account currAccount = account.get();
                int currAmount = currAccount.getMoneyAmount();
                if (currAmount >= amountToWithdraw) {
                    currAccount.setMoneyAmount(currAmount - amountToWithdraw);
                    System.out.printf("Amount %d withdrawn from account ID: %d%n", amountToWithdraw, accountId);
                } else {
                    System.out.printf("Not enough funds to withdraw: account id=%d, amount=%d, attemptedWithdraw=%d%n", accountId, currAmount, amountToWithdraw);
                }
            } else {
                System.out.println("There's no account with such id!");
            }
        } else {
            System.out.println("Amount should be greater than zero!");
        }
    }

    public void accountClose(int accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent()) {
            Account currAccount = account.get();
            List<Account> accountList = userService.getUserById(currAccount.getUserId()).get().getAccountList();
            if (accountList.size() > 1) {
                accountList.sort(Comparator.comparingInt(Account::getId));
                Account firstAccount = accountList.get(0);
                if (firstAccount.equals(currAccount)) {
                    firstAccount = accountList.get(1);
                }
                firstAccount.setMoneyAmount(firstAccount.getMoneyAmount() + currAccount.getMoneyAmount());
                accountList.remove(currAccount);
                accountRepository.deleteAccount(currAccount);
                System.out.printf("Account with ID=%d has been closed.%n", accountId);
            } else {
                System.out.printf("Could not close account with ID=%d because it's the only account for user with ID=%d%n", accountId, currAccount.getUserId());
            }
        } else {
            System.out.println("There's no account with such id!");
        }
    }

    public void accountTransfer(int sourceAccountId, int targetAccountId, int amountToTransfer) {
        Optional<Account> sourceAccount = accountRepository.findById(sourceAccountId);
        Optional<Account> targetAccount = accountRepository.findById(targetAccountId);
        if (sourceAccount.isPresent()) {
            if (targetAccount.isPresent()) {
                Account actualSourceAccount = sourceAccount.get();
                Account actualTargetAccount = targetAccount.get();

                int actualSourceAccountMoneyAmount = actualSourceAccount.getMoneyAmount();
                if (actualSourceAccount.getUserId().equals(actualTargetAccount.getUserId())) {
                    if (actualSourceAccountMoneyAmount >= amountToTransfer) {
                        actualSourceAccount.setMoneyAmount(actualSourceAccountMoneyAmount - amountToTransfer);
                    } else {
                        System.out.printf("Not enough funds to transfer: account id=%d, amount=%d, amountToTransfer=%d%n", sourceAccountId, actualSourceAccount.getMoneyAmount(), amountToTransfer);
                    }
                } else {
                    if (actualSourceAccountMoneyAmount >= amountToTransfer + amountToTransfer * transferCommission / 100) {
                        actualSourceAccount.setMoneyAmount(actualSourceAccountMoneyAmount - amountToTransfer - amountToTransfer * transferCommission / 100);
                    } else {
                        System.out.printf("Not enough funds to transfer: account id=%d, amount=%d, amountToTransfer=%d%n", sourceAccountId, actualSourceAccount.getMoneyAmount(), amountToTransfer);
                    }
                }
                actualTargetAccount.setMoneyAmount(actualTargetAccount.getMoneyAmount() + amountToTransfer);
                System.out.printf("Amount %d transferred from account ID %d to account ID %d.%n", amountToTransfer, sourceAccountId, targetAccountId);
            } else {
                System.out.printf("There's no account with ID=%d!%n", targetAccountId);
            }
        } else {
            System.out.printf("There's no account with ID=%d!%n", sourceAccountId);
        }
    }

}
