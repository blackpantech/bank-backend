package com.blackpantech.bank.infra.http;

import com.blackpantech.bank.domain.account.exceptions.AccountNotFoundException;
import com.blackpantech.bank.domain.transaction.Transaction;
import com.blackpantech.bank.domain.transaction.TransactionsService;
import com.blackpantech.bank.domain.transaction.exceptions.InsufficientFundsException;
import com.blackpantech.bank.domain.transaction.exceptions.InvalidTransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    private final TransactionsService transactionsService;

    public TransactionsController(final TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getAllTransactions() {
        return transactionsService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getAllTransactionsOfAccount(@PathVariable final long id) throws AccountNotFoundException {
        return transactionsService.getAllTransactionsOfAccount(id);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody final TransactionCreateRequest transactionCreateRequest)
            throws InsufficientFundsException, InvalidTransactionException, AccountNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionsService.createTransaction(
                        transactionCreateRequest.accountId(),
                        transactionCreateRequest.date(),
                        transactionCreateRequest.amount(),
                        transactionCreateRequest.transactionType()
                ));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    ResponseEntity<?> handleInsufficientFundsException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(InvalidTransactionException.class)
    ResponseEntity<?> handleInvalidTransactionException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<?> handleAccountNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

}
