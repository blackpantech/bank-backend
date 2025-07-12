package com.blackpantech.bank.infra.config;

import com.blackpantech.bank.domain.account.AccountsRepository;
import com.blackpantech.bank.domain.account.AccountsService;
import com.blackpantech.bank.domain.transaction.TransactionsRepository;
import com.blackpantech.bank.domain.transaction.TransactionsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankServicesConfiguration {

    @Bean
    public AccountsService accountsService(final AccountsRepository accountsRepository) {
        return new AccountsService(accountsRepository);
    }

    @Bean
    public TransactionsService transactionsService(final TransactionsRepository transactionsRepository,
                                                   final AccountsRepository accountsRepository) {
        return new TransactionsService(transactionsRepository, accountsRepository);
    }

}
