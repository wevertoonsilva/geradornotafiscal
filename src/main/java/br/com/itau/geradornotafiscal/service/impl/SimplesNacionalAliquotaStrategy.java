package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.service.AliquotaStrategy;

public class SimplesNacionalAliquotaStrategy implements AliquotaStrategy {
    @Override
    public double calcularAliquota(double valorTotalItens) {
        if (valorTotalItens < 1000) {
            return 0.03;
        } else if (valorTotalItens <= 2000) {
            return 0.07;
        } else if (valorTotalItens <= 5000) {
            return 0.13;
        } else {
            return 0.19;
        }
    }
}
