package com.taller.banco.service;

import com.taller.banco.domain.Account;
import com.taller.banco.domain.Transaction;
import com.taller.banco.exception.AccountNotFoundException;
import com.taller.banco.exception.validacionesdeentrada;
import com.taller.banco.repo.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountDepositTests {

    private AccountRepository repository;
    private BankingService service;
    private Account cuenta;

    @BeforeEach
    void setUp() {
        repository = mock(AccountRepository.class);
        service = new BankingService(repository);

        cuenta = new Account("123", "Naiker", BigDecimal.ZERO);

        when(repository.findById("123")).thenReturn(Optional.of(cuenta));
    }

    @Test
    void depositarMontoValidoAumentaSaldo() {
        cuenta.deposit(BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(100).setScale(2), cuenta.getBalance());
    }

    @Test
    void depositarMontoNegativoDebeFallar() {
        assertThrows(validacionesdeentrada.class,
                () -> cuenta.deposit(BigDecimal.valueOf(-50)));
    }

    @Test
    void depositarMontoCeroDebeFallar() {
        assertThrows(validacionesdeentrada.class,
                () -> cuenta.deposit(BigDecimal.ZERO));
    }

    @Test
    void depositoGeneraHistorialEnTransferencia() {
        Account destino = new Account("456", "Carlos", BigDecimal.ZERO);
        when(repository.findById("456")).thenReturn(Optional.of(destino));

        cuenta.deposit(BigDecimal.valueOf(200));

        service.transfer("123", "456", BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(100).setScale(2), cuenta.getBalance());
        assertEquals(BigDecimal.valueOf(100).setScale(2), destino.getBalance());

        Transaction t = service.getHistory().get(0);

        assertEquals(Transaction.Type.TRANSFER, t.getType());
        assertEquals("123", t.getSourceAccountId());
        assertEquals("456", t.getTargetAccountId());
        assertEquals(BigDecimal.valueOf(100).setScale(2), t.getAmount());
    }

    @Test
    void depositoCuentaInexistenteDebeFallar() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> service.transfer("999", "123", BigDecimal.valueOf(50)));
    }
}
