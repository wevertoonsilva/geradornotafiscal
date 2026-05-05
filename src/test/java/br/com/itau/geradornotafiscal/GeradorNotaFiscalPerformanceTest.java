package br.com.itau.geradornotafiscal;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class GeradorNotaFiscalPerformanceTest {

    @Autowired
    private GeradorNotaFiscalService geradorNotaFiscalService;

    @MockitoSpyBean
    private br.com.itau.geradornotafiscal.port.out.EstoquePort estoquePort;

    @Test
    public void deveMedirTempoExecucaoAte5Itens() {
        Pedido pedido = criarPedido(5);
        
        long inicio = System.currentTimeMillis();
        geradorNotaFiscalService.gerarNotaFiscal(pedido);
        long fim = System.currentTimeMillis();
        
        long duracao = fim - inicio;
        System.out.println("Tempo de execução (<= 5 itens): " + duracao + "ms");
        
        // Atualmente soma: 380 + 500 + 150 + 200 + 250 = 1480ms
        // Se for paralelo, deve ser ~500ms (máximo entre eles)
        assertTrue(duracao < 1000, "O tempo de execução deve ser menor que 1000ms (paralelizado)");
    }

    @Test
    public void deveMedirTempoExecucaoMaisDe5Itens() {
        Pedido pedido = criarPedido(6);
        
        long inicio = System.currentTimeMillis();
        geradorNotaFiscalService.gerarNotaFiscal(pedido);
        long fim = System.currentTimeMillis();
        
        long duracao = fim - inicio;
        System.out.println("Tempo de execução (> 5 itens): " + duracao + "ms");
        
        // Atualmente soma: 1480 + 5000 = 6480ms
        // Se for paralelo, deve ser ~5200ms
        assertTrue(duracao < 5500, "O tempo de execução deve ser menor que 5500ms (paralelizado)");
    }

    @Test
    public void deveContinuarMesmoComErroNaIntegracao() {
        Pedido pedido = criarPedido(5);
        
        doThrow(new RuntimeException("Erro simulado no estoque")).when(estoquePort).enviarNotaFiscalParaBaixaEstoque(any());
        
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);
        
        assertNotNull(notaFiscal, "A nota fiscal deve ser gerada mesmo com erro na integração");
        verify(estoquePort).enviarNotaFiscalParaBaixaEstoque(any());
    }

    private Pedido criarPedido(int numItens) {
        List<Item> itens = new ArrayList<>();
        for (int i = 0; i < numItens; i++) {
            Item item = new Item();
            item.setIdItem("item" + i);
            item.setDescricao("item" + i);
            item.setValorUnitario(new BigDecimal("100.00"));
            item.setQuantidade(1);
            itens.add(item);
        }

        Endereco endereco = Endereco.builder()
                .finalidade(Finalidade.ENTREGA)
                .regiao(Regiao.SUDESTE)
                .build();

        Destinatario destinatario = Destinatario.builder()
                .tipoPessoa(TipoPessoa.FISICA)
                .enderecos(Collections.singletonList(endereco))
                .build();

        return Pedido.builder()
                .valorTotalItens(new BigDecimal("100.00").multiply(new BigDecimal(numItens)))
                .valorFrete(new BigDecimal("10.00"))
                .destinatario(destinatario)
                .itens(itens)
                .build();
    }
}
