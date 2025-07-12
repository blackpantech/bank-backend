package com.blackpantech.bank.infra.jpa.account;

import com.blackpantech.bank.domain.account.Account;
import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JpaAccountsRepositoryTest {

    @Autowired
    AccountsJpaRepository accountsJpaRepository;

    @Autowired
    JpaAccountsRepository jpaAccountsRepository;

    @Test
    @DisplayName("should find all accounts")
    void shouldFindAllAccounts() {
        final List<Account> accounts = jpaAccountsRepository.getAllAccounts();

        assertEquals(3, accounts.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    @DisplayName("should find account with given ID")
    void shouldFindAccount(final long id) throws AccountNotFoundException {
        final Account account = jpaAccountsRepository.getAccount(id);

        assertNotNull(account);
    }

    @ParameterizedTest
    @ValueSource(longs = {4L, 90L, 200L})
    @DisplayName("should throw account not found exception")
    void shouldThrowAccountNotFound(final long id) {
        assertThrows(AccountNotFoundException.class, () -> jpaAccountsRepository.getAccount(id));
    }

}
