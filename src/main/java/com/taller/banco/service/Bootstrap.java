package com.taller.banco.service;

import com.taller.banco.domain.Account;
import com.taller.banco.repo.AccountRepository;

import java.math.BigDecimal;

public final class Bootstrap {
    private Bootstrap(){}

    public static void seed(AccountRepository repo) {
        repo.save(new Account("A001", "juan", new BigDecimal("1000")));
        repo.save(new Account("A002", "Mauro", new BigDecimal("500")));
        repo.save(new Account("A003", "Yionvani", new BigDecimal("0")));
    }
}