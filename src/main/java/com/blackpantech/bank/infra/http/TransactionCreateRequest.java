package com.blackpantech.bank.infra.http;

import com.blackpantech.bank.domain.transaction.TransactionType;

import java.time.LocalDateTime;

public record TransactionCreateRequest(long accountId, LocalDateTime date, float amount,
                                       TransactionType transactionType) {
}
