package br.com.itau.geradornotafiscal.service.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PessoaFisicaAliquotaStrategyTest {

    private final PessoaFisicaAliquotaStrategy strategy = new PessoaFisicaAliquotaStrategy();

    @ParameterizedTest
    @CsvSource({
            "499.99, 0.0000",
            "500.0, 0.1200",
            "1000.0, 0.1200",
            "2000.0, 0.1200",
            "2000.01, 0.1500",
            "3000.0, 0.1500",
            "3500.0, 0.1500",
            "3500.01, 0.1700",
            "5000.0, 0.1700"
    })
    void calcularAliquota(String valor, String expected) {
        BigDecimal expectedAliquota = new BigDecimal(expected).setScale(4, RoundingMode.HALF_UP);
        assertEquals(expectedAliquota, strategy.calcularAliquota(new BigDecimal(valor)));
    }
}
