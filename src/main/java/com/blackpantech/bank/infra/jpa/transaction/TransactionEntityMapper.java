package com.blackpantech.bank.infra.jpa.transaction;

import com.blackpantech.bank.domain.transaction.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionEntityMapper {

    Transaction TransactionEntityToTransaction(final TransactionEntity transactionEntity);

    List<Transaction> TransactionEntitiesToTransactions(final List<TransactionEntity> transactionEntities);

}
