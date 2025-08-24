package com.taller.banco.repo;

import com.taller.banco.domain.Account;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(String id);
    void save(Account account);
    boolean existsById(String id);
}
