package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.Endereco;
import br.com.itau.geradornotafiscal.model.Finalidade;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        BigDecimal resultado = calculadora.calcular(destinatario, new BigDecimal("100.00"));
        assertEquals(new BigDecimal("108.00"), resultado);
    }

    @Test
    public void deveCalcularFreteParaRegiaoNordeste() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.NORDESTE);
        BigDecimal resultado = calculadora.calcular(destinatario, new BigDecimal("100.00"));
        assertEquals(new BigDecimal("108.50"), resultado);
    }

    @Test
    public void deveCalcularFreteParaRegiaoCentroOeste() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.CENTRO_OESTE);
        BigDecimal resultado = calculadora.calcular(destinatario, new BigDecimal("100.00"));
        assertEquals(new BigDecimal("107.00"), resultado);
    }

    @Test
    public void deveCalcularFreteParaRegiaoSudeste() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.SUDESTE);
        BigDecimal resultado = calculadora.calcular(destinatario, new BigDecimal("100.00"));
        assertEquals(new BigDecimal("104.80"), resultado);
    }

    @Test
    public void deveCalcularFreteParaRegiaoSul() {
        Destinatario destinatario = criarDestinatarioComRegiao(Regiao.SUL);
        BigDecimal resultado = calculadora.calcular(destinatario, new BigDecimal("100.00"));
        assertEquals(new BigDecimal("106.00"), resultado);
    }

    @Test
    public void deveLancarExcecaoQuandoRegiaoNaoEncontrada() {
        Destinatario destinatario = Destinatario.builder()
                .enderecos(Collections.emptyList())
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(destinatario, new BigDecimal("100.00")));
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

        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(destinatario, new BigDecimal("100.00")));
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
