package com.taller.banco.domain;

import com.taller.banco.exception.validacionesdeentrada;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTests {

    @Test
    void normalizeDebeRedondearADosDecimales() {
        BigDecimal valor = Money.normalize(BigDecimal.valueOf(123.456));
        assertEquals(BigDecimal.valueOf(123.46), valor);
    }

    @Test
    void normalizeDeEnteroDebeMantenerDosDecimales() {
        BigDecimal valor = Money.normalize(BigDecimal.valueOf(100));
        assertEquals(new BigDecimal("100.00"), valor);
    }

    @Test
    void isPositiveConValorPositivoDebeSerTrue() {
        assertTrue(Money.isPositive(BigDecimal.valueOf(50)));
    }

    @Test
    void isPositiveConCeroDebeSerFalse() {
        assertFalse(Money.isPositive(BigDecimal.ZERO));
    }

    @Test
    void isPositiveConNegativoDebeSerFalse() {
        assertFalse(Money.isPositive(BigDecimal.valueOf(-1)));
    }

    @Test
    void depositarMontoInvalidoDebeLanzarExcepcion() {
        Account cuenta = new Account("ABC", BigDecimal.ZERO);
        assertThrows(validacionesdeentrada.class,
                () -> cuenta.deposit(BigDecimal.valueOf(-100)));
    }
}
