package com.blackpantech.bank.infra.http;

import com.blackpantech.bank.domain.account.Account;
import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import com.blackpantech.bank.domain.transaction.Transaction;
import com.blackpantech.bank.domain.transaction.TransactionType;
import com.blackpantech.bank.domain.transaction.TransactionsService;
import com.blackpantech.bank.domain.transaction.exceptions.InsufficientFundsException;
import com.blackpantech.bank.domain.transaction.exceptions.InvalidTransactionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionsController.class)
public class TransactionsControllerTest {

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

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TransactionsService transactionsService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("should get all transactions")
    void shouldGetAllAccounts() throws Exception {
        when(transactionsService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(transactions), JsonCompareMode.STRICT));

        verify(transactionsService).getAllTransactions();
        verifyNoMoreInteractions(transactionsService);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    @DisplayName("should get all transactions of account with given ID")
    void shouldGetAccount(final long id) throws Exception {
        when(transactionsService.getAllTransactionsOfAccount(id)).thenReturn(transactions.stream().filter(transaction -> transaction.accountId() == id).toList());

        mockMvc.perform(get("/transactions/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(transactions.stream().filter(transaction -> transaction.accountId() == id).toList()), JsonCompareMode.STRICT));

        verify(transactionsService).getAllTransactionsOfAccount(id);
        verifyNoMoreInteractions(transactionsService);
    }

    @Test
    @DisplayName("should return 404 when getting transactions of non existing account")
    void shouldNotFindAccount_whenGettingTransactionsOfAccount() throws Exception {
        when(transactionsService.getAllTransactionsOfAccount(anyLong())).thenThrow(new AccountNotFoundException(""));

        mockMvc.perform(get("/transactions/{id}", 0L)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(transactionsService).getAllTransactionsOfAccount(anyLong());
        verifyNoMoreInteractions(transactionsService);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2025-08-23T22:00:00, 23.0f, 'DEPOSIT'",
            "2, 2025-08-23T22:00:00, 245.76f, 'TRANSFER'",
            "1, 2025-08-23T22:00:00, -100f, 'WITHDRAW'",
    })
    @DisplayName("should create a new transaction")
    void shouldCreateTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType) throws Exception {
        Transaction expectedTransaction = new Transaction(0L, accountId, date, amount, transactionType);
        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest(accountId, date, amount, transactionType);
        when(transactionsService.createTransaction(accountId, date, amount, transactionType)).thenReturn(expectedTransaction);

        mockMvc.perform(post("/transactions")
                        .content(objectMapper.writeValueAsString(transactionCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedTransaction), JsonCompareMode.STRICT));

        verify(transactionsService).createTransaction(accountId, date, amount, transactionType);
        verifyNoMoreInteractions(transactionsService);
    }

    @ParameterizedTest
    @CsvSource({
            "4, 2025-08-23T22:00:00, 23.0f, 'DEPOSIT'",
            "90, 2025-08-23T22:00:00, 245.76f, 'TRANSFER'",
            "200, 2025-08-23T22:00:00, -100f, 'WITHDRAW'",
    })
    @DisplayName("should return 404 when creating transaction because of non existing account")
    void shouldNotFindAccount_whenCreatingTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType) throws Exception {
        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest(accountId, date, amount, transactionType);
        when(transactionsService.createTransaction(accountId, date, amount, transactionType)).thenThrow(new AccountNotFoundException(""));

        mockMvc.perform(post("/transactions")
                        .content(objectMapper.writeValueAsString(transactionCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(transactionsService).createTransaction(accountId, date, amount, transactionType);
        verifyNoMoreInteractions(transactionsService);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2025-08-23T22:00:00, -230.0f, 'WITHDRAW'",
            "2, 2025-08-23T22:00:00, -500f, 'TRANSFER'",
            "1, 2025-08-23T22:00:00, -300f, 'WITHDRAW'",
    })
    @DisplayName("should return 400 when creating transaction because of insufficient funds")
    void shouldHaveInsufficientFunds_whenCreatingTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType) throws Exception {
        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest(accountId, date, amount, transactionType);
        when(transactionsService.createTransaction(accountId, date, amount, transactionType)).thenThrow(new InsufficientFundsException(""));

        mockMvc.perform(post("/transactions")
                        .content(objectMapper.writeValueAsString(transactionCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(transactionsService).createTransaction(accountId, date, amount, transactionType);
        verifyNoMoreInteractions(transactionsService);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2025-08-23T22:00:00, 230.0f, 'WITHDRAW'",
            "2, 2025-08-23T22:00:00, -500f, 'DEPOSIT'",
            "1, 2025-08-23T22:00:00, -300f, 'DEPOSIT'",
    })
    @DisplayName("should return 400 when creating transactions of invalid transaction")
    void shouldHaveInvalid_whenCreatingTransaction(final long accountId, final LocalDateTime date, final float amount, final TransactionType transactionType) throws Exception {
        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest(accountId, date, amount, transactionType);
        when(transactionsService.createTransaction(accountId, date, amount, transactionType)).thenThrow(new InvalidTransactionException(""));

        mockMvc.perform(post("/transactions")
                        .content(objectMapper.writeValueAsString(transactionCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(transactionsService).createTransaction(accountId, date, amount, transactionType);
        verifyNoMoreInteractions(transactionsService);
    }

}
