package com.blackpantech.bank.infra.jpa.transaction;

import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import com.blackpantech.bank.domain.transaction.Transaction;
import com.blackpantech.bank.domain.transaction.TransactionType;
import com.blackpantech.bank.domain.transaction.TransactionsRepository;
import com.blackpantech.bank.infra.jpa.account.AccountEntity;
import com.blackpantech.bank.infra.jpa.account.AccountsJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaTransactionsRepository implements TransactionsRepository {

    private final TransactionsJpaRepository transactionsJpaRepository;

    private final AccountsJpaRepository accountsJpaRepository;

    private final TransactionEntityMapper transactionEntityMapper;

    public JpaTransactionsRepository(TransactionsJpaRepository transactionsJpaRepository,
                                     AccountsJpaRepository accountsJpaRepository,
                                     TransactionEntityMapper transactionEntityMapper) {
        this.transactionsJpaRepository = transactionsJpaRepository;
        this.accountsJpaRepository = accountsJpaRepository;
        this.transactionEntityMapper = transactionEntityMapper;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionEntityMapper.TransactionEntitiesToTransactions(transactionsJpaRepository.findAll());
    }

    @Override
    public List<Transaction> getAllTransactionsOfAccount(final long accountId) throws AccountNotFoundException {
        Optional<AccountEntity> optionalAccountEntity = accountsJpaRepository.findById(accountId);

        if (optionalAccountEntity.isEmpty()) {
            throw new AccountNotFoundException(String.format("Account with ID %s not found.", accountId));
        }

        return transactionEntityMapper.TransactionEntitiesToTransactions(transactionsJpaRepository.findAllByAccount(optionalAccountEntity.get()));
    }

    @Override
    public Transaction createTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType)
            throws AccountNotFoundException {
        Optional<AccountEntity> optionalAccountEntity = accountsJpaRepository.findById(accountId);

        if (optionalAccountEntity.isEmpty()) {
            throw new AccountNotFoundException(String.format("Account with ID %s not found.", accountId));
        }

        final TransactionEntity transactionEntity = new TransactionEntity(optionalAccountEntity.get(), date, amount, transactionType);

        return transactionEntityMapper.TransactionEntityToTransaction(transactionsJpaRepository.save(transactionEntity));
    }

}
