package br.com.itau.geradornotafiscal.service.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplesNacionalAliquotaStrategyTest {

    private final SimplesNacionalAliquotaStrategy strategy = new SimplesNacionalAliquotaStrategy();

    @ParameterizedTest
    @CsvSource({
            "999.99, 0.03",
            "1000.0, 0.07",
            "1500.0, 0.07",
            "2000.0, 0.07",
            "2000.01, 0.13",
            "3500.0, 0.13",
            "5000.0, 0.13",
            "5000.01, 0.19",
            "10000.0, 0.19"
    })
    void calcularAliquota(double valor, double expected) {
        assertEquals(expected, strategy.calcularAliquota(valor), 0.0001);
    }
}
