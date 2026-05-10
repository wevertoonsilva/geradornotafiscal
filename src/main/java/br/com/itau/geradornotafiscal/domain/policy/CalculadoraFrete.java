package br.com.itau.geradornotafiscal.domain.policy;

import br.com.itau.geradornotafiscal.domain.model.Destinatario;
import br.com.itau.geradornotafiscal.domain.model.Endereco;
import br.com.itau.geradornotafiscal.domain.model.Finalidade;
import br.com.itau.geradornotafiscal.domain.model.Regiao;


import java.math.BigDecimal;
import java.math.RoundingMode;


public class CalculadoraFrete {

    public BigDecimal calcular(Destinatario destinatario, BigDecimal valorFrete) {
        Regiao regiao = destinatario.getEnderecos().stream()
                .filter(endereco -> endereco.getFinalidade() == Finalidade.ENTREGA || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA)
                .map(Endereco::getRegiao)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada para o destinatário"));

        BigDecimal percentual = switch (regiao) {
            case NORTE -> new BigDecimal("1.08");
            case NORDESTE -> new BigDecimal("1.085");
            case CENTRO_OESTE -> new BigDecimal("1.07");
            case SUDESTE -> new BigDecimal("1.048");
            case SUL -> new BigDecimal("1.06");
        };

        return valorFrete.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }
}
