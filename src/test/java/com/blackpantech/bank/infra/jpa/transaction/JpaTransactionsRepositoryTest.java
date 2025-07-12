package com.blackpantech.bank.infra.jpa.transaction;

import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import com.blackpantech.bank.domain.transaction.Transaction;
import com.blackpantech.bank.domain.transaction.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JpaTransactionsRepositoryTest {

    @Autowired
    TransactionsJpaRepository transactionsJpaRepository;

    @Autowired
    JpaTransactionsRepository jpaTransactionsRepository;

    @Test
    @DisplayName("should find all transactions")
    void shouldFindAllTransactions() {
        final List<Transaction> transactions = jpaTransactionsRepository.getAllTransactions();

        assertEquals(transactionsJpaRepository.count(), transactions.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    @DisplayName("should find transactions with given account ID")
    void shouldFindAllTransactionsOfAccount(final long accountId) throws AccountNotFoundException {
        final List<Transaction> transactions = jpaTransactionsRepository.getAllTransactionsOfAccount(accountId);

        assertFalse(transactions.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(longs = {4L, 90L, 200L})
    @DisplayName("should not find new transaction because of non existing account")
    void shouldNotFindAccount_whenFindingAllTransactionsOfAccount(final long accountId) {
        assertThrows(AccountNotFoundException.class, () -> jpaTransactionsRepository.getAllTransactionsOfAccount(accountId));
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2025-08-23T22:00:00, 23.0f, 'DEPOSIT'",
            "2, 2025-08-23T22:00:00, 245.76f, 'TRANSFER'",
            "1, 2025-08-23T22:00:00, -100f, 'WITHDRAW'",
    })
    @DisplayName("should create a new transaction")
    void shouldCreateTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType)
            throws AccountNotFoundException {
        final Transaction transaction = jpaTransactionsRepository.createTransaction(accountId, date, amount, transactionType);

        assertNotNull(transaction);
    }

    @ParameterizedTest
    @CsvSource({
            "4, 2025-08-23T22:00:00, 23.0f, 'DEPOSIT'",
            "90, 2025-08-23T22:00:00, 245.76f, 'TRANSFER'",
            "200, 2025-08-23T22:00:00, -100f, 'WITHDRAW'",
    })
    @DisplayName("should not create new transaction because of non existing account")
    void shouldNotFindAccount_whenCreatingTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType) {
        assertThrows(AccountNotFoundException.class, () -> jpaTransactionsRepository.createTransaction(accountId, date, amount, transactionType));
    }

}
