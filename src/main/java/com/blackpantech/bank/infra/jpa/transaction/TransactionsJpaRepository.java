package com.blackpantech.bank.infra.jpa.transaction;

import com.blackpantech.bank.infra.jpa.account.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionsJpaRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByAccount(final AccountEntity account);

}
