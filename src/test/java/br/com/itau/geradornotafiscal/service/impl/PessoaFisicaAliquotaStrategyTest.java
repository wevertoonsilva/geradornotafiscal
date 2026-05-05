package br.com.itau.geradornotafiscal.service.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PessoaFisicaAliquotaStrategyTest {

    private final PessoaFisicaAliquotaStrategy strategy = new PessoaFisicaAliquotaStrategy();

    @ParameterizedTest
    @CsvSource({
            "499.99, 0.0",
            "500.0, 0.12",
            "1000.0, 0.12",
            "2000.0, 0.12",
            "2000.01, 0.15",
            "3000.0, 0.15",
            "3500.0, 0.15",
            "3500.01, 0.17",
            "5000.0, 0.17"
    })
    void calcularAliquota(double valor, double expected) {
        assertEquals(expected, strategy.calcularAliquota(valor), 0.0001);
    }
}
