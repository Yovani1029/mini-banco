package com.taller.banco.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends excepcionesdedominio {
    public InsufficientFundsException(BigDecimal saldo, BigDecimal monto) {
        super("Fondos insuficientes. Saldo: " + saldo + ", Monto: " + monto);
    }
}
