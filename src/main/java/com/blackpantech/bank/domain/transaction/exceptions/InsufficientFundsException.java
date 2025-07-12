package com.blackpantech.bank.domain.transaction.exceptions;

public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }

}
