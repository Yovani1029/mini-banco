package com.taller.banco.service;

import com.taller.banco.domain.Transaction;
import com.taller.banco.repo.InMemoryAccountRepository;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Registro de transacciones de transferencia")
public class testregistrotransferencia {

    private InMemoryAccountRepository repo;
    private BankingService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryAccountRepository();
        service = new BankingService(repo);
        // Crear cuentas iniciales con saldo
        repo.save(new com.taller.banco.domain.Account("A001", "Juan", new BigDecimal("1000.00")));
        repo.save(new com.taller.banco.domain.Account("A002", "Mauro", new BigDecimal("500.00")));
    }

    @Test
    @DisplayName("Una transferencia se registra en el historial como transacción tipo TRANSFER")
    void transfer_createsTransaction() {
        // Act
        BigDecimal monto = new BigDecimal("200.00");
        service.transfer("A001", "A002", monto);

        // Assert
        var history = service.getHistory();
        assertEquals(1, history.size(), "Debe existir 1 transacción registrada");

        Transaction tx = history.get(0);
        assertEquals(Transaction.Type.TRANSFER, tx.getType(), "El tipo debe ser TRANSFER");
        assertEquals("A001", tx.getSourceAccountId(), "El source debe ser la cuenta A001");
        assertEquals("A002", tx.getTargetAccountId(), "El target debe ser la cuenta A002");
        assertEquals(monto, tx.getAmount(), "El monto debe coincidir con la transferencia");
        assertNotNull(tx.getId(), "La transacción debe tener un ID");
        assertNotNull(tx.getTimestamp(), "La transacción debe tener timestamp");
    }

    @Test
    @DisplayName("Múltiples transferencias generan múltiples transacciones en el historial")
    void multipleTransfers_generateMultipleTransactions() {
        service.transfer("A001", "A002", new BigDecimal("100.00"));
        service.transfer("A002", "A001", new BigDecimal("50.00"));

        var history = service.getHistory();
        assertEquals(2, history.size(), "Deben existir 2 transacciones registradas");
        assertEquals(Transaction.Type.TRANSFER, history.get(0).getType());
        assertEquals(Transaction.Type.TRANSFER, history.get(1).getType());
    }
}
