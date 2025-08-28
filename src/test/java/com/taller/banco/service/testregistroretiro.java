package com.taller.banco.service;

import com.taller.banco.domain.Account;
import com.taller.banco.domain.Transaction;
import com.taller.banco.repo.InMemoryAccountRepository;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Registro de transacciones de retiro")
public class testregistroretiro {

    private InMemoryAccountRepository repo;
    private BankingService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryAccountRepository();
        service = new BankingService(repo);
        // Crear cuenta inicial con saldo 1000
        repo.save(new Account("A001", "Juan", new BigDecimal("1000.00")));
    }

    @Test
    @DisplayName("Un retiro se registra en el historial como transacción tipo WITHDRAW")
    void withdraw_createsTransaction() {
        // Act
        BigDecimal retiro = new BigDecimal("150.00");
        service.withdraw("A001", retiro);

        // Assert
        var history = service.getHistory();
        assertEquals(1, history.size(), "Debe existir 1 transacción registrada");

        Transaction tx = history.get(0);
        assertEquals(Transaction.Type.WITHDRAW, tx.getType(), "El tipo debe ser WITHDRAW");
        assertEquals("A001", tx.getSourceAccountId(), "El source debe ser la cuenta A001");
        assertNull(tx.getTargetAccountId(), "El target debe ser null en un retiro");
        assertEquals(retiro, tx.getAmount(), "El monto debe coincidir con el retiro");
        assertNotNull(tx.getId(), "La transacción debe tener un ID");
        assertNotNull(tx.getTimestamp(), "La transacción debe tener timestamp");
    }

    @Test
    @DisplayName("Múltiples retiros generan múltiples transacciones en el historial")
    void multipleWithdraws_generateMultipleTransactions() {
        service.withdraw("A001", new BigDecimal("100.00"));
        service.withdraw("A001", new BigDecimal("50.00"));

        var history = service.getHistory();
        assertEquals(2, history.size(), "Deben existir 2 transacciones registradas");
        assertEquals(Transaction.Type.WITHDRAW, history.get(0).getType());
        assertEquals(Transaction.Type.WITHDRAW, history.get(1).getType());
    }
}
