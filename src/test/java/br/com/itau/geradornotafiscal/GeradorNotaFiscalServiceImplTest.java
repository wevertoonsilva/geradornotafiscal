package br.com.itau.geradornotafiscal;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.port.out.EntregaPort;
import br.com.itau.geradornotafiscal.port.out.EstoquePort;
import br.com.itau.geradornotafiscal.port.out.FinanceiroPort;
import br.com.itau.geradornotafiscal.port.out.RegistroPort;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.CalculadoraFrete;
import br.com.itau.geradornotafiscal.service.impl.GeradorNotaFiscalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeradorNotaFiscalServiceImplTest {

    @InjectMocks
    private GeradorNotaFiscalServiceImpl geradorNotaFiscalService;

    @Mock
    private CalculadoraAliquotaProduto calculadoraAliquotaProduto;

    @Mock
    private CalculadoraFrete calculadoraFrete;

    @Mock
    private EstoquePort estoquePort;

    @Mock
    private RegistroPort registroPort;

    @Mock
    private EntregaPort entregaPort;

    @Mock
    private FinanceiroPort financeiroPort;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void shouldGenerateNotaFiscalWithCorrectAliquota(TipoPessoa tipoPessoa, RegimeTributacaoPJ regime, double valorTotal, double expectedAliquota) {
        // Arrange
        Item item = new Item();
        item.setValorUnitario(valorTotal);
        item.setQuantidade(1);
        List<Item> itens = Collections.singletonList(item);

        Endereco endereco = Endereco.builder()
                .finalidade(Finalidade.ENTREGA)
                .regiao(Regiao.SUDESTE)
                .build();

        Destinatario destinatario = Destinatario.builder()
                .tipoPessoa(tipoPessoa)
                .regimeTributacao(regime)
                .enderecos(Collections.singletonList(endereco))
                .build();

        Pedido pedido = Pedido.builder()
                .valorTotalItens(valorTotal)
                .valorFrete(100.0)
                .destinatario(destinatario)
                .itens(itens)
                .build();

        List<ItemNotaFiscal> itensNF = itens.stream().map(i -> ItemNotaFiscal.builder()
                .valorUnitario(i.getValorUnitario())
                .quantidade(i.getQuantidade())
                .valorTributoItem(i.getValorUnitario() * i.getQuantidade() * expectedAliquota)
                .build()).collect(Collectors.toList());

        when(calculadoraAliquotaProduto.calcularAliquota(eq(itens), eq(expectedAliquota))).thenReturn(itensNF);
        when(calculadoraFrete.calcular(eq(destinatario), anyDouble())).thenReturn(100.0 * 1.048);

        // Act
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        // Assert
        assertNotNull(notaFiscal);
        assertEquals(valorTotal, notaFiscal.getValorTotalItens());
        assertEquals(100.0 * 1.048, notaFiscal.getValorFrete(), 0.001);
        assertEquals(itensNF.size(), notaFiscal.getItens().size());
        assertEquals(itensNF.get(0).getValorTributoItem(), notaFiscal.getItens().get(0).getValorTributoItem());

        verify(estoquePort).enviarNotaFiscalParaBaixaEstoque(notaFiscal);
        verify(registroPort).registrarNotaFiscal(notaFiscal);
        verify(entregaPort).agendarEntrega(notaFiscal);
        verify(financeiroPort).enviarNotaFiscalParaContasReceber(notaFiscal);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // PF
                Arguments.of(TipoPessoa.FISICA, null, 499.99, 0.0),
                Arguments.of(TipoPessoa.FISICA, null, 500.0, 0.12),
                Arguments.of(TipoPessoa.FISICA, null, 2000.0, 0.12),
                Arguments.of(TipoPessoa.FISICA, null, 2000.01, 0.15),
                Arguments.of(TipoPessoa.FISICA, null, 3500.0, 0.15),
                Arguments.of(TipoPessoa.FISICA, null, 3500.01, 0.17),

                // PJ Simples Nacional
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 999.99, 0.03),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 1000.0, 0.07),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 2000.0, 0.07),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 2000.01, 0.13),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 5000.0, 0.13),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 5000.01, 0.19),

                // PJ Lucro Real
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, 999.99, 0.03),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, 1000.0, 0.09),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, 2000.0, 0.09),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, 2000.01, 0.15),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, 5000.0, 0.15),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, 5000.01, 0.20),

                // PJ Lucro Presumido
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, 999.99, 0.03),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, 1000.0, 0.09),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, 2000.0, 0.09),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, 2000.01, 0.16),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, 5000.0, 0.16),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, 5000.01, 0.20)
        );
    }
}