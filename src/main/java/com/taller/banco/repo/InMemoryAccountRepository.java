package com.taller.banco.repo;

import com.taller.banco.domain.Account;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> data = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public void save(Account account) {
        data.put(account.getId(), account);
    }

    @Override
    public boolean existsById(String id) {
        return data.containsKey(id);
    }
}
