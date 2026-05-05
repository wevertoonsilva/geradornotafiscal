package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.Endereco;
import br.com.itau.geradornotafiscal.model.Finalidade;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;

@Component
public class CalculadoraFrete {

    public double calcular(Destinatario destinatario, double valorFrete) {
        Regiao regiao = destinatario.getEnderecos().stream()
                .filter(endereco -> endereco.getFinalidade() == Finalidade.ENTREGA || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA)
                .map(Endereco::getRegiao)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada para o destinatário"));

        double percentual = switch (regiao) {
            case NORTE -> 1.08;
            case NORDESTE -> 1.085;
            case CENTRO_OESTE -> 1.07;
            case SUDESTE -> 1.048;
            case SUL -> 1.06;
        };

        return valorFrete * percentual;
    }
}
