package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.service.AliquotaStrategy;

public class PessoaFisicaAliquotaStrategy implements AliquotaStrategy {
    @Override
    public double calcularAliquota(double valorTotalItens) {
        if (valorTotalItens < 500) {
            return 0;
        } else if (valorTotalItens <= 2000) {
            return 0.12;
        } else if (valorTotalItens <= 3500) {
            return 0.15;
        } else {
            return 0.17;
        }
    }
}
