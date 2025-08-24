package com.taller.banco.domain;

import com.taller.banco.exception.InsufficientFundsException;
import com.taller.banco.exception.validacionesdeentrada;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final String id;
    private final String owner; // para contexto (no usamos login aquí)
    private BigDecimal balance;

    public Account(String id, String owner, BigDecimal initialBalance) {
        if (id == null || id.isBlank()) throw new validacionesdeentrada("id inválido");
        if (owner == null || owner.isBlank()) throw new validacionesdeentrada("owner inválido");
        BigDecimal normalized = Money.normalize(initialBalance == null ? BigDecimal.ZERO : initialBalance);
        if (normalized.compareTo(BigDecimal.ZERO) < 0) throw new validacionesdeentrada("Saldo inicial no puede ser negativo");

        this.id = id;
        this.owner = owner;
        this.balance = normalized;
    }

    public String getId() { return id; }
    public String getOwner() { return owner; }

    public synchronized BigDecimal getBalance() { return balance; }

    public synchronized void deposit(BigDecimal amount) {
        if (!Money.isPositive(amount)) throw new validacionesdeentrada("El depósito debe ser > 0");
        balance = balance.add(Money.normalize(amount));
    }

    public synchronized void withdraw(BigDecimal amount) {
        if (!Money.isPositive(amount)) throw new validacionesdeentrada("El retiro debe ser > 0");
        BigDecimal normalized = Money.normalize(amount);
        if (balance.compareTo(normalized) < 0) {
            throw new InsufficientFundsException(balance, normalized);
        }
        balance = balance.subtract(normalized);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return id.equals(account.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}