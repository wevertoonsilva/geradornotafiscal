package br.com.itau.geradornotafiscal.service.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LucroPresumidoAliquotaStrategyTest {

    private final LucroPresumidoAliquotaStrategy strategy = new LucroPresumidoAliquotaStrategy();

    @ParameterizedTest
    @CsvSource({
            "999.99, 0.03",
            "1000.0, 0.09",
            "1500.0, 0.09",
            "2000.0, 0.09",
            "2000.01, 0.16",
            "3500.0, 0.16",
            "5000.0, 0.16",
            "5000.01, 0.20",
            "10000.0, 0.20"
    })
    void calcularAliquota(double valor, double expected) {
        assertEquals(expected, strategy.calcularAliquota(valor), 0.0001);
    }
}
