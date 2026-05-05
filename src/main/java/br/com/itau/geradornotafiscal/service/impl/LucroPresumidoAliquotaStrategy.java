package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.service.AliquotaStrategy;

public class LucroPresumidoAliquotaStrategy implements AliquotaStrategy {
    @Override
    public double calcularAliquota(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return 0.03;
        } else if (valorTotalItens <= 2000) {
            return 0.09;
        } else if (valorTotalItens <= 5000) {
            return 0.16;
        } else {
            return 0.20;
        }
    }
}
