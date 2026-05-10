package br.com.itau.geradornotafiscal.domain.policy;

import br.com.itau.geradornotafiscal.domain.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;

import java.util.Map;

public class AliquotaStrategyFactory {

    private static final Map<RegimeTributacaoPJ, AliquotaStrategy> PJ_STRATEGIES = Map.of(
            RegimeTributacaoPJ.SIMPLES_NACIONAL, new SimplesNacionalAliquotaStrategy(),
            RegimeTributacaoPJ.LUCRO_REAL, new LucroRealAliquotaStrategy(),
            RegimeTributacaoPJ.LUCRO_PRESUMIDO, new LucroPresumidoAliquotaStrategy()
    );

    private static final AliquotaStrategy PF_STRATEGY = new PessoaFisicaAliquotaStrategy();

    public static AliquotaStrategy getStrategy(TipoPessoa tipoPessoa, RegimeTributacaoPJ regimeTributacao) {
        if (tipoPessoa == TipoPessoa.FISICA) {
            return PF_STRATEGY;
        } else if (tipoPessoa == TipoPessoa.JURIDICA) {
            if (regimeTributacao == RegimeTributacaoPJ.OUTROS) {
                throw new IllegalArgumentException("Regime de tributação inválido: OUTROS");
            }
            AliquotaStrategy strategy = PJ_STRATEGIES.get(regimeTributacao);
            if (strategy == null) {
                throw new IllegalArgumentException("Regime de tributação não suportado: " + regimeTributacao);
            }
            return strategy;
        }
        throw new IllegalArgumentException("Tipo de pessoa não suportado: " + tipoPessoa);
    }
}
