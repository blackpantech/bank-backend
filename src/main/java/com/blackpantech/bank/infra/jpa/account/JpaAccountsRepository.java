package com.blackpantech.bank.infra.jpa.account;

import com.blackpantech.bank.domain.account.Account;
import com.blackpantech.bank.domain.account.AccountsRepository;
import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAccountsRepository implements AccountsRepository {

    private final AccountsJpaRepository accountsJpaRepository;

    private final AccountEntityMapper accountEntityMapper;

    public JpaAccountsRepository(AccountsJpaRepository accountsJpaRepository, AccountEntityMapper accountEntityMapper) {
        this.accountsJpaRepository = accountsJpaRepository;
        this.accountEntityMapper = accountEntityMapper;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountEntityMapper.AccountEntitiesToAccounts(accountsJpaRepository.findAll());
    }

    @Override
    public Account getAccount(final long id) throws AccountNotFoundException {
        final Optional<AccountEntity> optionalAccountEntity = accountsJpaRepository.findById(id);

        if (optionalAccountEntity.isEmpty()) {
            throw new AccountNotFoundException(String.format("Account with ID %s not found.", id));
        }

        return accountEntityMapper.AccountEntityToAccount(optionalAccountEntity.get());
    }

}
