package com.blackpantech.bank.infra.jpa.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsJpaRepository extends JpaRepository<AccountEntity, Long> {
}
