package com.taller.banco.service;

import com.taller.banco.domain.Transaction;
import com.taller.banco.exception.AccountNotFoundException;
import com.taller.banco.exception.validacionesdeentrada;
import com.taller.banco.repo.InMemoryAccountRepository;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Transferencias (BankingService.transfer)")
class Pruebasdetransferencia {

    private InMemoryAccountRepository repo;
    private BankingService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryAccountRepository();
        service = new BankingService(repo);
        // Cuentas base
        repo.save(new com.taller.banco.domain.Account("A001", "Juan",   new BigDecimal("1000.00")));
        repo.save(new com.taller.banco.domain.Account("A002", "Mauro", new BigDecimal("500.00")));
        repo.save(new com.taller.banco.domain.Account("A003", "Yionvani",  new BigDecimal("0.00")));
    }

    @Test
    @DisplayName("Transferencia válida actualiza ambos saldos")
    void transfer_happyPath() {
        service.transfer("A001", "A002", new BigDecimal("200.00"));
        assertAll(
                () -> assertEquals(new BigDecimal("800.00"), repo.findById("A001").orElseThrow().getBalance()),
                () -> assertEquals(new BigDecimal("700.00"), repo.findById("A002").orElseThrow().getBalance())
        );
    }

    @Test
    @DisplayName("Monto 0 o negativo → ValidationException")
    void transfer_invalidAmounts() {
        assertAll(
                () -> assertThrows(validacionesdeentrada.class,
                        () -> service.transfer("A001", "A002", new BigDecimal("0"))),
                () -> assertThrows(validacionesdeentrada.class,
                        () -> service.transfer("A001", "A002", new BigDecimal("-1.00")))
        );
    }

    @Test
    @DisplayName("No se puede transferir a la misma cuenta")
    void transfer_sameAccount() {
        assertThrows(validacionesdeentrada.class,
                () -> service.transfer("A001", "A001", new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Cuenta origen o destino inexistente → AccountNotFoundException")
    void transfer_accountNotFound() {
        assertAll(
                () -> assertThrows(AccountNotFoundException.class,
                        () -> service.transfer("NOEXISTE", "A002", new BigDecimal("10.00"))),
                () -> assertThrows(AccountNotFoundException.class,
                        () -> service.transfer("A001", "NOEXISTE", new BigDecimal("10.00")))
        );
    }

    @Test
    @DisplayName("Fondos insuficientes: no cambia ninguno de los dos saldos")
    void transfer_insufficientFunds_noStateChange() {
        var beforeSrc = repo.findById("A003").orElseThrow().getBalance(); // 0.00
        var beforeDst = repo.findById("A002").orElseThrow().getBalance(); // 500.00

        // A003 no tiene fondos
        assertThrows(RuntimeException.class, // puede ser InsufficientFundsException desde withdraw interno
                () -> service.transfer("A003", "A002", new BigDecimal("10.00")));

        var afterSrc = repo.findById("A003").orElseThrow().getBalance();
        var afterDst = repo.findById("A002").orElseThrow().getBalance();

        assertEquals(beforeSrc, afterSrc, "Saldo de origen no debe cambiar");
        assertEquals(beforeDst, afterDst, "Saldo de destino no debe cambiar");
    }

    @Test
    @DisplayName("Redondeo: transferir 0.005 mueve 0.01 (HALF_UP a 2 decimales)")
    void transfer_rounding() {
        service.transfer("A001", "A002", new BigDecimal("0.005"));
        assertAll(
                () -> assertEquals(new BigDecimal("999.99"), repo.findById("A001").orElseThrow().getBalance()),
                () -> assertEquals(new BigDecimal("500.01"), repo.findById("A002").orElseThrow().getBalance())
        );
    }

    @Test
    @DisplayName("Se registra una transacción de tipo TRANSFER en el historial")
    void transfer_addsHistoryEntry() {
        service.transfer("A001", "A002", new BigDecimal("50.00"));
        var history = service.getHistory();
        assertFalse(history.isEmpty(), "El historial no puede estar vacío");
        var last = history.get(history.size() - 1);
        assertEquals(Transaction.Type.TRANSFER, last.getType());
        assertEquals("A001", last.getSourceAccountId());
        assertEquals("A002", last.getTargetAccountId());
        assertEquals(new BigDecimal("50.00"), last.getAmount());
    }
}