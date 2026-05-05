package br.com.itau.geradornotafiscal.service;

import java.math.BigDecimal;

public interface AliquotaStrategy {
    BigDecimal calcularAliquota(BigDecimal valorTotalItens);
}
