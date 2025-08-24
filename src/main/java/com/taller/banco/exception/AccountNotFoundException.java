package com.taller.banco.exception;

public class AccountNotFoundException extends excepcionesdedominio {
    public AccountNotFoundException(String id) {
        super("Cuenta no encontrada: " + id);
    }
}
