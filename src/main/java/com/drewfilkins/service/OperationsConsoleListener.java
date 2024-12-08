package com.drewfilkins.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class OperationsConsoleListener {

    private static final String USER_CREATE = "USER_CREATE";
    private static final String SHOW_ALL_USERS = "SHOW_ALL_USERS";
    private static final String ACCOUNT_CREATE = "ACCOUNT_CREATE";
    private static final String ACCOUNT_CLOSE = "ACCOUNT_CLOSE";
    private static final String ACCOUNT_DEPOSIT = "ACCOUNT_DEPOSIT";
    private static final String ACCOUNT_TRANSFER = "ACCOUNT_TRANSFER";
    private static final String ACCOUNT_WITHDRAW = "ACCOUNT_WITHDRAW";

    private final UserService userService;
    private final AccountService accountService;

//    private final Scanner scanner = new Scanner(System.in);

    public OperationsConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostConstruct
    public void runAfterStartup() {
        readOperation();
    }

    private void readOperation() {
        try(Scanner scanner = new Scanner(System.in)) {
            System.out.println("""
                    \nPlease enter one of operation type:
                    -ACCOUNT_CREATE
                    -SHOW_ALL_USERS
                    -ACCOUNT_CLOSE
                    -ACCOUNT_WITHDRAW
                    -ACCOUNT_DEPOSIT
                    -ACCOUNT_TRANSFER
                    -USER_CREATE""");
            String operation = scanner.nextLine();
            switch (operation) {
                case USER_CREATE -> userCreate(scanner);
                case SHOW_ALL_USERS -> showAllUsers();
                case ACCOUNT_CREATE -> accountCreate(scanner);
                case ACCOUNT_CLOSE -> accountClose(scanner);
                case ACCOUNT_WITHDRAW -> accountWithdraw(scanner);
                case ACCOUNT_DEPOSIT -> accountDeposit(scanner);
                case ACCOUNT_TRANSFER -> accountTransfer(scanner);
                default -> defaultOperation();
            }
        }
    }

    private void defaultOperation() {
        System.out.println("Undefined operation!");
        readOperation();
    }

    private void userCreate(Scanner scanner) {
        System.out.println("Enter login for new user:");
        String login = scanner.nextLine();
        userService.userCreate(login);
        readOperation();
    }

    private void showAllUsers() {
        System.out.println("List of all users:");
        userService.getAllUsers().forEach(u -> System.out.println(u));
        readOperation();
    }

    private void accountCreate(Scanner scanner) {
        System.out.println("Enter the user id for which to create an account:");
        while (scanner.hasNext()) {
            if (!scanner.hasNextInt()) {
                System.out.println("Illegal argument! User id should be an integer!");
                scanner.next();
            } else {
                accountService.createAccountByUserId(scanner.nextInt());
                break;
            }
        }
        readOperation();
    }

    private void accountClose(Scanner scanner) {
        System.out.println("Enter account ID to close:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Account id should be an integer!");
            scanner.next();
        }
        int accountId = scanner.nextInt();
        accountService.accountClose(accountId);
        readOperation();
    }

    private void accountWithdraw(Scanner scanner) {
        System.out.println("Enter account ID:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Account id should be an integer!");
            scanner.next();
        }
        int accountId = scanner.nextInt();
        System.out.println("Enter amount to withdraw:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Amount should be an integer!");
            scanner.next();
        }
        int amount = scanner.nextInt();
        accountService.accountWithdraw(accountId, amount);
        readOperation();
    }

    private void accountDeposit(Scanner scanner) {
        System.out.println("Enter account ID:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Account id should be an integer!");
            scanner.next();
        }
        int accountId = scanner.nextInt();
        System.out.println("Enter amount to deposit:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Amount should be an integer!");
            scanner.next();
        }
        int amount = scanner.nextInt();
        accountService.accountDeposit(accountId, amount);
        readOperation();
    }

    private void accountTransfer(Scanner scanner) {
        System.out.println("Enter source account ID:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Account id should be an integer!");
            scanner.next();
        }
        int sourceAccountId = scanner.nextInt();
        System.out.println("Enter target account ID:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Account id should be an integer!");
            scanner.next();
        }
        int targetAccountId = scanner.nextInt();
        System.out.println("Enter amount to transfer:");
        while (!scanner.hasNextInt()) {
            System.out.println("Illegal argument! Amount should be an integer!");
            scanner.next();
        }
        int amount = scanner.nextInt();
        accountService.accountTransfer(sourceAccountId, targetAccountId, amount);
        readOperation();
    }
}