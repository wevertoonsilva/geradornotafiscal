package br.com.itau.geradornotafiscal.application.usecase;

import br.com.itau.geradornotafiscal.domain.model.*;
import br.com.itau.geradornotafiscal.application.port.out.EntregaPort;
import br.com.itau.geradornotafiscal.application.port.out.EstoquePort;
import br.com.itau.geradornotafiscal.application.port.out.FinanceiroPort;
import br.com.itau.geradornotafiscal.application.port.out.RegistroPort;
import br.com.itau.geradornotafiscal.domain.policy.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.domain.policy.CalculadoraFrete;
import br.com.itau.geradornotafiscal.domain.policy.NotaFiscalFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GerarNotaFiscalUseCaseTest {

    @InjectMocks
    private GerarNotaFiscalUseCase geradorNotaFiscalService;

    @Mock
    private CalculadoraAliquotaProduto calculadoraAliquotaProduto;

    @Mock
    private CalculadoraFrete calculadoraFrete;

    @Mock
    private NotaFiscalFactory notaFiscalFactory;

    @Mock
    private EstoquePort estoquePort;

    @Mock
    private RegistroPort registroPort;

    @Mock
    private EntregaPort entregaPort;

    @Mock
    private FinanceiroPort financeiroPort;

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        geradorNotaFiscalService = new GerarNotaFiscalUseCase(
                calculadoraAliquotaProduto,
                calculadoraFrete,
                notaFiscalFactory,
                estoquePort,
                registroPort,
                entregaPort,
                financeiroPort,
                meterRegistry
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void shouldGenerateNotaFiscalWithCorrectAliquota(TipoPessoa tipoPessoa, RegimeTributacaoPJ regime, BigDecimal valorTotal, BigDecimal expectedAliquota) {
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
                .valorFrete(new BigDecimal("100.00"))
                .destinatario(destinatario)
                .itens(itens)
                .build();

        List<ItemNotaFiscal> itensNF = itens.stream().map(i -> ItemNotaFiscal.builder()
                .valorUnitario(i.getValorUnitario())
                .quantidade(i.getQuantidade())
                .valorTributoItem(i.getValorUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())).multiply(expectedAliquota).setScale(2, RoundingMode.HALF_UP))
                .build()).toList();

        when(calculadoraAliquotaProduto.calcularAliquota(eq(itens), eq(expectedAliquota))).thenReturn(itensNF);
        BigDecimal valorFreteEsperado = new BigDecimal("100.00").multiply(new BigDecimal("1.048")).setScale(2, RoundingMode.HALF_UP);
        when(calculadoraFrete.calcular(eq(destinatario), any(BigDecimal.class))).thenReturn(valorFreteEsperado);

        NotaFiscal notaFiscalMock = NotaFiscal.builder()
                .idNotaFiscal("teste-id")
                .valorTotalItens(valorTotal)
                .valorFrete(valorFreteEsperado)
                .itens(itensNF)
                .destinatario(destinatario)
                .build();
        when(notaFiscalFactory.criar(eq(pedido), eq(itensNF), any(BigDecimal.class))).thenReturn(notaFiscalMock);

        // Act
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        // Assert
        assertNotNull(notaFiscal);
        assertEquals(valorTotal, notaFiscal.getValorTotalItens());
        assertEquals(valorFreteEsperado, notaFiscal.getValorFrete());
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
                Arguments.of(TipoPessoa.FISICA, null, new BigDecimal("499.99"), new BigDecimal("0.0000").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.FISICA, null, new BigDecimal("500.00"), new BigDecimal("0.1200").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.FISICA, null, new BigDecimal("2000.00"), new BigDecimal("0.1200").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.FISICA, null, new BigDecimal("2000.01"), new BigDecimal("0.1500").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.FISICA, null, new BigDecimal("3500.00"), new BigDecimal("0.1500").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.FISICA, null, new BigDecimal("3500.01"), new BigDecimal("0.1700").setScale(4, RoundingMode.HALF_UP)),

                // PJ Simples Nacional
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, new BigDecimal("999.99"), new BigDecimal("0.0300").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, new BigDecimal("1000.00"), new BigDecimal("0.0700").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, new BigDecimal("2000.00"), new BigDecimal("0.0700").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, new BigDecimal("2000.01"), new BigDecimal("0.1300").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, new BigDecimal("5000.00"), new BigDecimal("0.1300").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, new BigDecimal("5000.01"), new BigDecimal("0.1900").setScale(4, RoundingMode.HALF_UP)),

                // PJ Lucro Real
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, new BigDecimal("999.99"), new BigDecimal("0.0300").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, new BigDecimal("1000.00"), new BigDecimal("0.0900").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, new BigDecimal("2000.00"), new BigDecimal("0.0900").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, new BigDecimal("2000.01"), new BigDecimal("0.1500").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, new BigDecimal("5000.00"), new BigDecimal("0.1500").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_REAL, new BigDecimal("5000.01"), new BigDecimal("0.2000").setScale(4, RoundingMode.HALF_UP)),

                // PJ Lucro Presumido
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, new BigDecimal("999.99"), new BigDecimal("0.0300").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, new BigDecimal("1000.00"), new BigDecimal("0.0900").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, new BigDecimal("2000.00"), new BigDecimal("0.0900").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, new BigDecimal("2000.01"), new BigDecimal("0.1600").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, new BigDecimal("5000.00"), new BigDecimal("0.1600").setScale(4, RoundingMode.HALF_UP)),
                Arguments.of(TipoPessoa.JURIDICA, RegimeTributacaoPJ.LUCRO_PRESUMIDO, new BigDecimal("5000.01"), new BigDecimal("0.2000").setScale(4, RoundingMode.HALF_UP))
        );
    }
}
