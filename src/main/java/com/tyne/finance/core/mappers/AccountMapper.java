package com.tyne.finance.core.mappers;

import com.tyne.finance.core.dto.AccountDTO;
import com.tyne.finance.core.models.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    List<AccountDTO> accountsToAccountDTOS(List<Account> accounts);

    Account accountDTOToAccount(AccountDTO accountDTO);

    AccountDTO accountToAccountDTO(Account account);
}
