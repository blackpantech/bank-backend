package com.blackpantech.bank.domain.transaction.exceptions;

public class InvalidTransactionException extends Exception {

    public InvalidTransactionException(String message) {
        super(message);
    }

}
