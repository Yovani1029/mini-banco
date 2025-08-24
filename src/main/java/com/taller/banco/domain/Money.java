package com.taller.banco.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    private Money(){}

    public static BigDecimal normalize(BigDecimal value) {
        if (value == null) throw new IllegalArgumentException("El valor monetario no puede ser nulo");
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isPositive(BigDecimal value) {
        return normalize(value).compareTo(BigDecimal.ZERO) > 0;
    }
}