package com.taller.banco.service;

import com.taller.banco.exception.AccountNotFoundException;
import com.taller.banco.exception.InsufficientFundsException;
import com.taller.banco.exception.validacionesdeentrada;
import com.taller.banco.repo.InMemoryAccountRepository;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Retiros (BankingService.withdraw)")
class Pruebasderetirada {

    private InMemoryAccountRepository repo;
    private BankingService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryAccountRepository();
        service = new BankingService(repo);
        // Cargas iniciales: A001 con 1000.00
        repo.save(new com.taller.banco.domain.Account("A001", "Juan", new BigDecimal("1000.00")));
    }

    @Test
    @DisplayName("Retiro válido reduce el saldo correctamente")
    void withdraw_happyPath() {
        service.withdraw("A001", new BigDecimal("100.00"));
        var balance = repo.findById("A001").orElseThrow().getBalance();
        assertEquals(new BigDecimal("900.00"), balance);
    }

    @Test
    @DisplayName("Retiro por el saldo exacto deja la cuenta en 0.00")
    void withdraw_exactBalance() {
        service.withdraw("A001", new BigDecimal("1000.00"));
        var balance = repo.findById("A001").orElseThrow().getBalance();
        assertEquals(new BigDecimal("0.00"), balance);
    }

    @Test
    @DisplayName("Monto 0 o negativo → ValidationException")
    void withdraw_invalidAmounts() {
        assertAll(
                () -> assertThrows(validacionesdeentrada.class,
                        () -> service.withdraw("A001", new BigDecimal("0"))),
                () -> assertThrows(validacionesdeentrada.class,
                        () -> service.withdraw("A001", new BigDecimal("-1"))),
                () -> assertThrows(validacionesdeentrada.class,
                        () -> service.withdraw("A001", new BigDecimal("-0.01")))
        );
    }

    @Test
    @DisplayName("Fondos insuficientes → InsufficientFundsException (saldo no cambia)")
    void withdraw_insufficientFunds() {
        var before = repo.findById("A001").orElseThrow().getBalance();
        assertThrows(InsufficientFundsException.class,
                () -> service.withdraw("A001", new BigDecimal("2000.00")));
        var after = repo.findById("A001").orElseThrow().getBalance();
        assertEquals(before, after, "El saldo no debe cambiar si hay fondos insuficientes");
    }

    @Test
    @DisplayName("Cuenta inexistente → AccountNotFoundException")
    void withdraw_accountNotFound() {
        assertThrows(AccountNotFoundException.class,
                () -> service.withdraw("NOEXISTE", new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Redondeo: retirar 0.005 descuenta 0.01 (HALF_UP a 2 decimales)")
    void withdraw_rounding() {
        service.withdraw("A001", new BigDecimal("0.005"));
        var balance = repo.findById("A001").orElseThrow().getBalance();
        assertEquals(new BigDecimal("999.99"), balance);
    }
}