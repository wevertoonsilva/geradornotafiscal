package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.Endereco;
import br.com.itau.geradornotafiscal.model.Finalidade;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculadoraFreteTest {

    private CalculadoraFrete calculadora;

    @BeforeEach
    public void setUp() {
        calculadora = new CalculadoraFrete();
    }

    @Test
    public void deveCalcularFreteParaRegiaoNorte() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.NORTE);
        double resultado = calculadora.calcular(destinatario, 100.0);
        assertEquals(108.0, resultado, 0.001);
    }

    @Test
    public void deveCalcularFreteParaRegiaoNordeste() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.NORDESTE);
        double resultado = calculadora.calcular(destinatario, 100.0);
        assertEquals(108.5, resultado, 0.001);
    }

    @Test
    public void deveCalcularFreteParaRegiaoCentroOeste() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.CENTRO_OESTE);
        double resultado = calculadora.calcular(destinatario, 100.0);
        assertEquals(107.0, resultado, 0.001);
    }

    @Test
    public void deveCalcularFreteParaRegiaoSudeste() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.SUDESTE);
        double resultado = calculadora.calcular(destinatario, 100.0);
        assertEquals(104.8, resultado, 0.001);
    }

    @Test
    public void deveCalcularFreteParaRegiaoSul() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.SUL);
        double resultado = calculadora.calcular(destinatario, 100.0);
        assertEquals(106.0, resultado, 0.001);
    }

    @Test
    public void deveLancarExcecaoQuandoRegiaoNaoEncontrada() {
        Destinatario destinatario = Destinatario.builder()
                .enderecos(Collections.emptyList())
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(destinatario, 100.0));
    }

    @Test
    public void deveLancarExcecaoQuandoRegiaoForNula() {
        Endereco endereco = Endereco.builder()
                .finalidade(Finalidade.ENTREGA)
                .regiao(null)
                .build();
        Destinatario destinatario = Destinatario.builder()
                .enderecos(List.of(endereco))
                .build();

        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(destinatario, 100.0));
    }

    private Destinatario criarDestinatarioComRegiao(Regiao regiao) {
        Endereco endereco = Endereco.builder()
                .finalidade(Finalidade.ENTREGA)
                .regiao(regiao)
                .build();
        return Destinatario.builder()
                .enderecos(List.of(endereco))
                .build();
    }
}
