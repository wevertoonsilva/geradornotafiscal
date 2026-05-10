package br.com.itau.geradornotafiscal.domain.policy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplesNacionalAliquotaStrategyTest {

    private final SimplesNacionalAliquotaStrategy strategy = new SimplesNacionalAliquotaStrategy();

    @ParameterizedTest
    @CsvSource({
            "999.99, 0.0300",
            "1000.0, 0.0700",
            "1500.0, 0.0700",
            "2000.0, 0.0700",
            "2000.01, 0.1300",
            "3500.0, 0.1300",
            "5000.0, 0.1300",
            "5000.01, 0.1900",
            "10000.0, 0.1900"
    })
    void calcularAliquota(String valor, String expected) {
        BigDecimal expectedAliquota = new BigDecimal(expected).setScale(4, RoundingMode.HALF_UP);
        assertEquals(expectedAliquota, strategy.calcularAliquota(new BigDecimal(valor)));
    }
}
