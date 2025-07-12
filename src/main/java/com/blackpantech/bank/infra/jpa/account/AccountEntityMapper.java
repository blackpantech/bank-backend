package com.blackpantech.bank.infra.jpa.account;

import com.blackpantech.bank.domain.account.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountEntityMapper {

    Account AccountEntityToAccount(final AccountEntity accountEntity);

    AccountEntity AccountToAccountEntity(final Account account);

    List<Account> AccountEntitiesToAccounts(List<AccountEntity> accountEntities);

}
