package br.com.itau.geradornotafiscal.domain.policy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LucroPresumidoAliquotaStrategyTest {

    private final LucroPresumidoAliquotaStrategy strategy = new LucroPresumidoAliquotaStrategy();

    @ParameterizedTest
    @CsvSource({
            "999.99, 0.0300",
            "1000.0, 0.0900",
            "1500.0, 0.0900",
            "2000.0, 0.0900",
            "2000.01, 0.1600",
            "3500.0, 0.1600",
            "5000.0, 0.1600",
            "5000.01, 0.2000",
            "10000.0, 0.2000"
    })
    void calcularAliquota(String valor, String expected) {
        BigDecimal expectedAliquota = new BigDecimal(expected).setScale(4, RoundingMode.HALF_UP);
        assertEquals(expectedAliquota, strategy.calcularAliquota(new BigDecimal(valor)));
    }
}
