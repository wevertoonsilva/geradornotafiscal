package br.com.itau.geradornotafiscal.domain.policy;

import java.math.BigDecimal;

public interface AliquotaStrategy {
    BigDecimal calcularAliquota(BigDecimal valorTotalItens);
}
