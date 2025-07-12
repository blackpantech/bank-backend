package com.blackpantech.bank.infra.jpa.transaction;

import com.blackpantech.bank.domain.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionEntityMapper {

    @Mapping(source = "account.id", target = "accountId")
    Transaction TransactionEntityToTransaction(final TransactionEntity transactionEntity);

    List<Transaction> TransactionEntitiesToTransactions(final List<TransactionEntity> transactionEntities);

}
