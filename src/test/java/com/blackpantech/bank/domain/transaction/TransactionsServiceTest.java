package com.blackpantech.bank.domain.transaction;

import com.blackpantech.bank.domain.account.Account;
import com.blackpantech.bank.domain.account.AccountsRepository;
import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import com.blackpantech.bank.domain.transaction.exceptions.InsufficientFundsException;
import com.blackpantech.bank.domain.transaction.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class TransactionsServiceTest {

    @Mock
    final TransactionsRepository transactionsRepository = mock(TransactionsRepository.class);

    @Mock
    final AccountsRepository accountsRepository = mock(AccountsRepository.class);

    final TransactionsService transactionsService = new TransactionsService(transactionsRepository, accountsRepository);

    final List<Account> accounts = List.of(
            new Account(1L, 223.42f, new ArrayList<>()),
            new Account(2L, 324.9f, new ArrayList<>()),
            new Account(3L, 24.67f, new ArrayList<>())
    );

    final List<Transaction> transactions = List.of(
            new Transaction(1L, accounts.getFirst().id(), LocalDateTime.now(), -23f, TransactionType.TRANSFER),
            new Transaction(2L, accounts.get(1).id(), LocalDateTime.now(), 23f, TransactionType.TRANSFER),
            new Transaction(3L, accounts.getLast().id(), LocalDateTime.now(), -20f, TransactionType.WITHDRAW),
            new Transaction(4L, accounts.get(1).id(), LocalDateTime.now(), 50f, TransactionType.DEPOSIT)
    );

    @Test
    @DisplayName("should get all transactions")
    void shouldGetAllTransactions() {
        Mockito.when(transactionsRepository.getAllTransactions()).thenReturn(transactions);
        List<Transaction> returnedTransactions = transactionsService.getAllTransactions();

        assertEquals(transactions, returnedTransactions);
        verify(transactionsRepository).getAllTransactions();
        verifyNoMoreInteractions(transactionsRepository);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    @DisplayName("should get all transactions of account with given ID")
    void shouldGetAllTransactionsOfAccount(final long accountId) throws AccountNotFoundException {
        Mockito.when(transactionsRepository.getAllTransactionsOfAccount(accountId))
                .thenReturn(transactions.stream()
                        .filter(transaction -> transaction.accountId() == accountId)
                        .toList()
                );
        List<Transaction> returnedTransactions = transactionsService.getAllTransactionsOfAccount(accountId);

        assertEquals(transactions.stream()
                        .filter(transaction -> transaction.accountId() == accountId)
                        .toList(),
                returnedTransactions);
        verify(transactionsRepository).getAllTransactionsOfAccount(accountId);
        verifyNoMoreInteractions(transactionsRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2025-08-23T22:00:00, 23.0f, 'DEPOSIT'",
            "2, 2025-08-23T22:00:00, 245.76f, 'TRANSFER'",
            "1, 2025-08-23T22:00:00, -100f, 'WITHDRAW'",
    })
    @DisplayName("should create a new transaction")
    void shouldCreateTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType)
            throws AccountNotFoundException, InsufficientFundsException, InvalidTransactionException {
        Mockito.when(transactionsRepository.createTransaction(accountId, date, amount, transactionType))
                .thenReturn(new Transaction(5, accountId, date, amount, transactionType));
        Mockito.when(accountsRepository.getAccount(accountId)).thenReturn(accounts.get((int) (accountId - 1L)));
        Transaction returnedTransaction =
                transactionsService.createTransaction(accountId, date, amount, transactionType);

        assertEquals(new Transaction(5, accountId, date, amount, transactionType), returnedTransaction);
        verify(accountsRepository).getAccount(accountId);
        verify(transactionsRepository).createTransaction(accountId, date, amount, transactionType);
        verifyNoMoreInteractions(accountsRepository, transactionsRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2025-08-23T22:00:00, -230.0f, 'WITHDRAW'",
            "2, 2025-08-23T22:00:00, -500f, 'TRANSFER'",
            "1, 2025-08-23T22:00:00, -300f, 'WITHDRAW'",
    })
    @DisplayName("should not create new transaction because of insufficient funds")
    void shouldHaveInsufficientFunds(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType)
            throws AccountNotFoundException {
        Mockito.when(accountsRepository.getAccount(accountId)).thenReturn(accounts.get((int) (accountId - 1L)));

        assertThrows(InsufficientFundsException.class,
                () -> transactionsService.createTransaction(accountId, date, amount, transactionType));
        verify(accountsRepository).getAccount(accountId);
        verifyNoMoreInteractions(accountsRepository, transactionsRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2025-08-23T22:00:00, 230.0f, 'WITHDRAW'",
            "2, 2025-08-23T22:00:00, -500f, 'DEPOSIT'",
            "1, 2025-08-23T22:00:00, -300f, 'DEPOSIT'",
    })
    @DisplayName("should not create new transaction because transaction is invalid.")
    void shouldNotCreateTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType)
            throws AccountNotFoundException {
        Mockito.when(accountsRepository.getAccount(accountId)).thenReturn(accounts.get((int) (accountId - 1L)));

        assertThrows(InvalidTransactionException.class,
                () -> transactionsService.createTransaction(accountId, date, amount, transactionType));
        verify(accountsRepository).getAccount(accountId);
        verifyNoMoreInteractions(accountsRepository, transactionsRepository);
    }

}
