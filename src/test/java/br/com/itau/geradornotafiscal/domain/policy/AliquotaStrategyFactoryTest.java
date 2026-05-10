package br.com.itau.geradornotafiscal.domain.policy;

import br.com.itau.geradornotafiscal.domain.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AliquotaStrategyFactoryTest {

    @Test
    void shouldReturnPessoaFisicaStrategy() {
        AliquotaStrategy strategy = AliquotaStrategyFactory.getStrategy(TipoPessoa.FISICA, null);
        assertInstanceOf(PessoaFisicaAliquotaStrategy.class, strategy);
    }

    @Test
    void shouldReturnSimplesNacionalStrategy() {
        AliquotaStrategy strategy = AliquotaStrategyFactory.getStrategy(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL);
        assertInstanceOf(SimplesNacionalAliquotaStrategy.class, strategy);
    }

    @Test
    void shouldReturnLucroRealStrategy() {
        AliquotaStrategy strategy = AliquotaStrategyFactory.getStrategy(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL);
        assertInstanceOf(LucroRealAliquotaStrategy.class, strategy);
    }

    @Test
    void shouldReturnLucroPresumidoStrategy() {
        AliquotaStrategy strategy = AliquotaStrategyFactory.getStrategy(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO);
        assertInstanceOf(LucroPresumidoAliquotaStrategy.class, strategy);
    }

    @Test
    void shouldThrowExceptionForOutros() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AliquotaStrategyFactory.getStrategy(TipoPessoa.JURIDICA, RegimeTributacaoPJ.OUTROS));
        assertEquals("Regime de tributação inválido: OUTROS", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForUnsupportedTipoPessoa() {
        assertThrows(IllegalArgumentException.class, () ->
                AliquotaStrategyFactory.getStrategy(null, null));
    }
}
