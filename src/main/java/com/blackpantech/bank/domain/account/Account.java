package com.blackpantech.bank.domain.account;

import com.blackpantech.bank.domain.transaction.Transaction;

import java.util.List;

public record Account(long id, float balance, List<Transaction> transactions) {
}
