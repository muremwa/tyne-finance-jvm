package com.tyne.finance.core.services;

import com.tyne.finance.core.dto.AccountDTO;
import com.tyne.finance.core.mappers.AccountMapper;
import com.tyne.finance.core.models.Account;
import com.tyne.finance.core.repositories.CoreAccountRepository;
import com.tyne.finance.core.repositories.CoreUserRepository;
import com.tyne.finance.dto.TyneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final CoreAccountRepository accountRepository;
    private final CoreUserRepository userRepository;
    private final AccountMapper accountMapper;

    public TyneResponse<List<AccountDTO>> getAccounts(String username) {
        List<AccountDTO> accounts = this.accountMapper.accountsToAccountDTOS(
                this.accountRepository.findAccountsByUserUsername(username)
        );

        return TyneResponse.<List<AccountDTO>>builder()
                .message("Success")
                .status(true)
                .data(accounts)
                .build();
    }

    public TyneResponse<AccountDTO> updateAccount(AccountDTO accountDTO, String username) {
        Account account = this.accountMapper.accountDTOToAccount(accountDTO);

        if (accountDTO.getAccountID() != null) {
            Optional<Account> preAccount = this.accountRepository.findById(accountDTO.getAccountID());

            if (preAccount.isEmpty()) {
                return TyneResponse.<AccountDTO>builder()
                        .message("Account to update not found")
                        .status(false)
                        .data(null)
                        .build();
            } else if (!preAccount.get().getUser().getUsername().equals(username) || !preAccount.get().isActive()) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }

            Account preAcc = preAccount.get();
            if (accountDTO.getAccountNumber() == null) {
                account.setAccountNumber(preAcc.getAccountNumber());
            }

            if (accountDTO.getAccountProvider() == null) {
                account.setAccountProvider(preAcc.getAccountProvider());
            }

            account.setAccountType(preAcc.getAccountType());
            account.setUser(preAcc.getUser());
            account.setDateAdded(preAcc.getDateAdded());
            account.setActive(preAcc.isActive());
            account.setBalance(preAcc.getBalance());
            account.setLastBalanceUpdate(preAcc.getLastBalanceUpdate());
        } else {
            account.setUser(this.userRepository.findUserByUsername(username));
            account.setDateAdded(new Timestamp(new Date().getTime()));
            account.setActive(true);
        }

        account.setDateModified(new Timestamp(new Date().getTime()));
        AccountDTO savedAccountDTO = this.accountMapper.accountToAccountDTO(
                this.accountRepository.save(account)
        );

        return TyneResponse.<AccountDTO>builder()
                .message("Saved successfully")
                .status(true)
                .data(savedAccountDTO)
                .build();
    }

    public TyneResponse<Boolean> updateAccountStatus(BigInteger accountID, String username) {
        Account account = this.accountRepository.findById(accountID).orElse(null);
        var builder = TyneResponse.<Boolean>builder();

        if (account == null) {
           return builder.status(Boolean.FALSE).message("Account does not exist").build();
        }

        if (!account.getUser().getUsername().equals(username)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }

        this.accountRepository.updateAccountActiveStatus(account.getAccountID(), account.isActive()? 0: 1);
        return builder
                .message(account.isActive()? "Account deactivated" : "Account activated")
                .status(true)
                .data(Boolean.TRUE)
                .build();
    }

}
