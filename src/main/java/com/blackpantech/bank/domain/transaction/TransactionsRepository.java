package com.blackpantech.bank.domain.transaction;

import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionsRepository {

    List<Transaction> getAllTransactions();

    List<Transaction> getAllTransactionsOfAccount(final long accountId) throws AccountNotFoundException;

    Transaction createTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType) throws AccountNotFoundException;

}
