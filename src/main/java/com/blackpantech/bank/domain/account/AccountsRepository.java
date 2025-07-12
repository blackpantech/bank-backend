package com.blackpantech.bank.domain.account;

import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;

import java.util.List;

public interface AccountsRepository {

    List<Account> getAllAccounts();

    Account getAccount(final long id) throws AccountNotFoundException;

}
