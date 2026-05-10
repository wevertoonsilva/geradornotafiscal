package br.com.itau.geradornotafiscal.infrastructure.config;

import br.com.itau.geradornotafiscal.domain.policy.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.domain.policy.CalculadoraFrete;
import br.com.itau.geradornotafiscal.domain.policy.NotaFiscalFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CalculadoraAliquotaProduto calculadoraAliquotaProduto() {
        return new CalculadoraAliquotaProduto();
    }

    @Bean
    public CalculadoraFrete calculadoraFrete() {
        return new CalculadoraFrete();
    }

    @Bean
    public NotaFiscalFactory notaFiscalFactory() {
        return new NotaFiscalFactory();
    }
}
