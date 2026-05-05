package br.com.itau.geradornotafiscal.service.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LucroRealAliquotaStrategyTest {

    private final LucroRealAliquotaStrategy strategy = new LucroRealAliquotaStrategy();

    @ParameterizedTest
    @CsvSource({
            "999.99, 0.03",
            "1000.0, 0.09",
            "1500.0, 0.09",
            "2000.0, 0.09",
            "2000.01, 0.15",
            "3500.0, 0.15",
            "5000.0, 0.15",
            "5000.01, 0.20",
            "10000.0, 0.20"
    })
    void calcularAliquota(double valor, double expected) {
        assertEquals(expected, strategy.calcularAliquota(valor), 0.0001);
    }
}
