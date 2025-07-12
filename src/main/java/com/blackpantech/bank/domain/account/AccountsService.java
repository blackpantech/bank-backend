package com.blackpantech.bank.domain.account;

import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;

import java.util.List;

public class AccountsService {

    private final AccountsRepository accountsRepository;

    public AccountsService(final AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public List<Account> getAllAccounts() {
        return accountsRepository.getAllAccounts();
    }

    public Account getAccount(final long id) throws AccountNotFoundException {
        return accountsRepository.getAccount(id);
    }

}
