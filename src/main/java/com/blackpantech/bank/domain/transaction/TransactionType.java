package com.blackpantech.bank.domain.transaction;

public enum TransactionType {

    WITHDRAW("WITHDRAW"), DEPOSIT("DEPOSIT"), TRANSFER("TRANSFER");

    public final String type;

    TransactionType(final String type) {
        this.type = type;
    }

}
