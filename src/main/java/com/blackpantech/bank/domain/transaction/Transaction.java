package com.blackpantech.bank.domain.transaction;

import java.time.LocalDateTime;

public record Transaction(long id, long accountId, LocalDateTime date, float amount, TransactionType transactionType) {
}
