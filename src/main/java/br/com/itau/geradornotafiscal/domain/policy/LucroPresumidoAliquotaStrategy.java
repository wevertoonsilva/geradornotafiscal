package br.com.itau.geradornotafiscal.domain.policy;

import br.com.itau.geradornotafiscal.domain.policy.AliquotaStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LucroPresumidoAliquotaStrategy implements AliquotaStrategy {
    @Override
    public BigDecimal calcularAliquota(BigDecimal valorTotalItens) {
        if (valorTotalItens.compareTo(new BigDecimal("1000")) < 0) {
            return new BigDecimal("0.03").setScale(4, RoundingMode.HALF_UP);
        } else if (valorTotalItens.compareTo(new BigDecimal("2000")) <= 0) {
            return new BigDecimal("0.09").setScale(4, RoundingMode.HALF_UP);
        } else if (valorTotalItens.compareTo(new BigDecimal("5000")) <= 0) {
            return new BigDecimal("0.16").setScale(4, RoundingMode.HALF_UP);
        } else {
            return new BigDecimal("0.20").setScale(4, RoundingMode.HALF_UP);
        }
    }
}
