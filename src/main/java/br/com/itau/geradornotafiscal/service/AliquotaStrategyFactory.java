package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import br.com.itau.geradornotafiscal.service.impl.LucroPresumidoAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.impl.LucroRealAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.impl.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.impl.SimplesNacionalAliquotaStrategy;

import java.util.HashMap;
import java.util.Map;

public class AliquotaStrategyFactory {

    private static final Map<RegimeTributacaoPJ, AliquotaStrategy> PJ_STRATEGIES = new HashMap<>();

    static {
        PJ_STRATEGIES.put(RegimeTributacaoPJ.SIMPLES_NACIONAL, new SimplesNacionalAliquotaStrategy());
        PJ_STRATEGIES.put(RegimeTributacaoPJ.LUCRO_REAL, new LucroRealAliquotaStrategy());
        PJ_STRATEGIES.put(RegimeTributacaoPJ.LUCRO_PRESUMIDO, new LucroPresumidoAliquotaStrategy());
    }

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
