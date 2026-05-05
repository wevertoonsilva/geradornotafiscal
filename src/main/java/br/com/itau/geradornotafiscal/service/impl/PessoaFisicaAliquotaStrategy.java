package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.service.AliquotaStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PessoaFisicaAliquotaStrategy implements AliquotaStrategy {
    @Override
    public BigDecimal calcularAliquota(BigDecimal valorTotalItens) {
        if (valorTotalItens.compareTo(new BigDecimal("500")) < 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        } else if (valorTotalItens.compareTo(new BigDecimal("2000")) <= 0) {
            return new BigDecimal("0.12").setScale(4, RoundingMode.HALF_UP);
        } else if (valorTotalItens.compareTo(new BigDecimal("3500")) <= 0) {
            return new BigDecimal("0.15").setScale(4, RoundingMode.HALF_UP);
        } else {
            return new BigDecimal("0.17").setScale(4, RoundingMode.HALF_UP);
        }
    }
}
