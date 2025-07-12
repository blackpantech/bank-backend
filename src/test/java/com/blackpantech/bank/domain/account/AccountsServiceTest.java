package com.blackpantech.bank.domain.account;

import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class AccountsServiceTest {

    @Mock
    final AccountsRepository accountsRepository = mock(AccountsRepository.class);

    final AccountsService accountsService = new AccountsService(accountsRepository);

    final List<Account> accounts = List.of(
            new Account(1L, 223.42f, new ArrayList<>()),
            new Account(2L, 324.9f, new ArrayList<>()),
            new Account(3L, 24.67f, new ArrayList<>())
    );

    @Test
    @DisplayName("should get all accounts")
    void shouldGetAllAccounts() {
        Mockito.when(accountsRepository.getAllAccounts()).thenReturn(accounts);
        List<Account> returnedAccounts = accountsService.getAllAccounts();

        assertEquals(accounts, returnedAccounts);
        verify(accountsRepository).getAllAccounts();
        verifyNoMoreInteractions(accountsRepository);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    @DisplayName("should get account with given ID")
    void shouldGetAccount(final long id) throws AccountNotFoundException {
        Mockito.when(accountsRepository.getAccount(id))
                .thenReturn(accounts.stream()
                        .filter(account -> account.id() == id)
                        .findFirst()
                        .orElseThrow()
                );
        Account returnedAccount = accountsService.getAccount(id);

        assertEquals(accounts.stream()
                        .filter(account -> account.id() == id)
                        .findFirst()
                        .orElseThrow(),
                returnedAccount);
        verify(accountsRepository).getAccount(id);
        verifyNoMoreInteractions(accountsRepository);
    }

}
