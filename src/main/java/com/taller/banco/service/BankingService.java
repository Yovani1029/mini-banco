package com.taller.banco.service;

import com.taller.banco.domain.Account;
import com.taller.banco.domain.Money;
import com.taller.banco.domain.Transaction;
import com.taller.banco.exception.AccountNotFoundException;
import com.taller.banco.exception.validacionesdeentrada;
import com.taller.banco.repo.AccountRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BankingService {
    private final AccountRepository accounts;
    private final List<Transaction> history = new ArrayList<>();

    public BankingService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public void withdraw(String accountId, BigDecimal amount) {
        if (!Money.isPositive(amount)) throw new validacionesdeentrada("Monto de retiro inválido");

        Account acc = accounts.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        acc.withdraw(amount);
        history.add(new Transaction(Transaction.Type.WITHDRAW, accountId, null, Money.normalize(amount)));
        accounts.save(acc);
    }

    public void transfer(String sourceId, String targetId, BigDecimal amount) {
        if (!Money.isPositive(amount)) throw new validacionesdeentrada("Monto de transferencia inválido");
        if (sourceId.equals(targetId)) throw new validacionesdeentrada("No se puede transferir a la misma cuenta");

        Account src = accounts.findById(sourceId).orElseThrow(() -> new AccountNotFoundException(sourceId));
        Account dst = accounts.findById(targetId).orElseThrow(() -> new AccountNotFoundException(targetId));

        // Orden de bloqueo determinístico para evitar deadlock
        Account first  = Comparator.comparing(Account::getId).compare(src, dst) <= 0 ? src : dst;
        Account second = first == src ? dst : src;

        synchronized (first) {
            synchronized (second) {
                // Intenta retirar; si falla por fondos, lanza excepción y nada cambia
                src.withdraw(amount);
                dst.deposit(amount);
            }
        }
        history.add(new Transaction(Transaction.Type.TRANSFER, sourceId, targetId, Money.normalize(amount)));
        accounts.save(src);
        accounts.save(dst);
    }

    public List<Transaction> getHistory() { return List.copyOf(history); }
}