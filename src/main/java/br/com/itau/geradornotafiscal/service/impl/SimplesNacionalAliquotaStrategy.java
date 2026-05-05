package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.service.AliquotaStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SimplesNacionalAliquotaStrategy implements AliquotaStrategy {
    @Override
    public BigDecimal calcularAliquota(BigDecimal valorTotalItens) {
        if (valorTotalItens.compareTo(new BigDecimal("1000")) < 0) {
            return new BigDecimal("0.03").setScale(4, RoundingMode.HALF_UP);
        } else if (valorTotalItens.compareTo(new BigDecimal("2000")) <= 0) {
            return new BigDecimal("0.07").setScale(4, RoundingMode.HALF_UP);
        } else if (valorTotalItens.compareTo(new BigDecimal("5000")) <= 0) {
            return new BigDecimal("0.13").setScale(4, RoundingMode.HALF_UP);
        } else {
            return new BigDecimal("0.19").setScale(4, RoundingMode.HALF_UP);
        }
    }
}
