package com.blackpantech.bank.infra.http;

import com.blackpantech.bank.domain.account.Account;
import com.blackpantech.bank.domain.account.AccountsService;
import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    public AccountsController(final AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAllAccounts() {
        return accountsService.getAllAccounts();
    }

    @GetMapping("/{id}")
    ResponseEntity<Account> getAccount(@PathVariable final long id) throws AccountNotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsService.getAccount(id));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<?> handleAccountNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

}
