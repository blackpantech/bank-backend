package com.blackpantech.bank.infra.http;

import com.blackpantech.bank.domain.account.Account;
import com.blackpantech.bank.domain.account.AccountsService;
import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountsController.class)
public class AccountsControllerTest {

    final List<Account> accounts = List.of(
            new Account(1L, 223.42f, new ArrayList<>()),
            new Account(2L, 324.9f, new ArrayList<>()),
            new Account(3L, 24.67f, new ArrayList<>())
    );

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AccountsService accountsService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("should get all accounts")
    void shouldGetAllAccounts() throws Exception {
        when(accountsService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/accounts")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(accounts), JsonCompareMode.STRICT));

        verify(accountsService).getAllAccounts();
        verifyNoMoreInteractions(accountsService);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    @DisplayName("should get all accounts")
    void shouldGetAccount(final long id) throws Exception {
        when(accountsService.getAccount(id)).thenReturn(accounts.get((int) (id - 1L)));

        mockMvc.perform(get("/accounts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(accounts.get((int) (id - 1L))), JsonCompareMode.STRICT));

        verify(accountsService).getAccount(id);
        verifyNoMoreInteractions(accountsService);
    }

    @Test
    @DisplayName("should return 404 when getting an account")
    void shouldNotFindTask_whenGetTask() throws Exception {
        when(accountsService.getAccount(anyLong())).thenThrow(new AccountNotFoundException(""));

        mockMvc.perform(get("/accounts/{id}", 0L)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(accountsService).getAccount(anyLong());
        verifyNoMoreInteractions(accountsService);
    }

}
