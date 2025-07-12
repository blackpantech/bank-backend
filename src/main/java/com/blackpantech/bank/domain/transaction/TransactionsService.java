package com.blackpantech.bank.domain.transaction;

import com.blackpantech.bank.domain.account.Account;
import com.blackpantech.bank.domain.account.AccountsRepository;
import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import com.blackpantech.bank.domain.transaction.exceptions.InsufficientFundsException;
import com.blackpantech.bank.domain.transaction.exceptions.InvalidTransactionException;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionsService {

    private final TransactionsRepository transactionsRepository;

    private final AccountsRepository accountsRepository;

    public TransactionsService(final TransactionsRepository transactionsRepository,
                               final AccountsRepository accountsRepository) {
        this.transactionsRepository = transactionsRepository;
        this.accountsRepository = accountsRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionsRepository.getAllTransactions();
    }

    public List<Transaction> getAllTransactionsOfAccount(final long accountId) throws AccountNotFoundException {
        return transactionsRepository.getAllTransactionsOfAccount(accountId);
    }

    public Transaction createTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType)
            throws AccountNotFoundException, InsufficientFundsException, InvalidTransactionException {
        final Account account = accountsRepository.getAccount(accountId);
        return getValidTransaction(account, date, amount, transactionType);
    }

    private Transaction getValidTransaction(final Account account, final LocalDateTime date, final float amount, final TransactionType transactionType)
            throws InvalidTransactionException, InsufficientFundsException, AccountNotFoundException {
        if (transactionType == TransactionType.DEPOSIT && amount < 0f) {
            throw new InvalidTransactionException("Deposit amount is negative.");
        }
        if (transactionType == TransactionType.WITHDRAW && amount >= 0f) {
            throw new InvalidTransactionException("Withdraw amount is positive.");
        }
        if ((transactionType == TransactionType.WITHDRAW || transactionType == TransactionType.TRANSFER)
                && (account.balance() + amount < 0f)) {
            throw new InsufficientFundsException("Insufficient funds to make transaction.");
        }

        return transactionsRepository.createTransaction(account.id(), date, amount, transactionType);
    }

}
