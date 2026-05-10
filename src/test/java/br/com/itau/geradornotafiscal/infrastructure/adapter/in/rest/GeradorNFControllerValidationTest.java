package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.*;
import br.com.itau.geradornotafiscal.application.port.in.GerarNotaFiscalPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GeradorNFControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GerarNotaFiscalPort notaFiscalService;

    private String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deveRetornar400QuandoPedidoSemDestinatario() throws Exception {
        PedidoRequest pedido = PedidoRequest.builder()
                .idPedido(1)
                .valorTotalItens(new BigDecimal("100.00"))
                .valorFrete(new BigDecimal("10.00"))
                .itens(Collections.singletonList(new PedidoRequest.ItemRequest("1", "Item 1", new BigDecimal("100.00"), 1)))
                .build();

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(pedido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição Inválida"))
                .andExpect(jsonPath("$.errors").value(containsString("destinatario:")));
    }

    @Test
    void deveRetornar400QuandoPJSemRegimeTributacao() throws Exception {
        PedidoRequest.DestinatarioRequest destinatario = PedidoRequest.DestinatarioRequest.builder()
                .nome("Empresa JURIDICA")
                .tipoPessoa(TipoPessoa.JURIDICA)
                .regimeTributacao(null)
                .enderecos(Collections.singletonList(new PedidoRequest.EnderecoRequest()))
                .build();

        PedidoRequest pedido = PedidoRequest.builder()
                .idPedido(1)
                .valorTotalItens(new BigDecimal("100.00"))
                .valorFrete(new BigDecimal("10.00"))
                .itens(Collections.singletonList(new PedidoRequest.ItemRequest("1", "Item 1", new BigDecimal("100.00"), 1)))
                .destinatario(destinatario)
                .build();

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(pedido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(containsString("destinatario.regimeTributacao: PJ deve ter regime de tributação informado")));
    }

    @Test
    void deveRetornar400QuandoTotaisNaoReconciliam() throws Exception {
        PedidoRequest.DestinatarioRequest destinatario = PedidoRequest.DestinatarioRequest.builder()
                .nome("Pessoa FISICA")
                .tipoPessoa(TipoPessoa.FISICA)
                .enderecos(Collections.singletonList(new PedidoRequest.EnderecoRequest()))
                .build();

        PedidoRequest pedido = PedidoRequest.builder()
                .idPedido(1)
                .valorTotalItens(new BigDecimal("100.00")) // Total informado: 100
                .valorFrete(new BigDecimal("10.00"))
                .itens(Collections.singletonList(new PedidoRequest.ItemRequest("1", "Item 1", new BigDecimal("50.00"), 1))) // Total real: 50
                .destinatario(destinatario)
                .build();

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(pedido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(containsString("O valor total dos itens não corresponde à soma dos itens")));
    }

    @Test
    void deveRetornar400QuandoSemEnderecos() throws Exception {
        PedidoRequest.DestinatarioRequest destinatario = PedidoRequest.DestinatarioRequest.builder()
                .nome("Pessoa FISICA")
                .tipoPessoa(TipoPessoa.FISICA)
                .enderecos(Collections.emptyList()) // Sem endereços
                .build();

        PedidoRequest pedido = PedidoRequest.builder()
                .idPedido(1)
                .valorTotalItens(new BigDecimal("100.00"))
                .valorFrete(new BigDecimal("10.00"))
                .itens(Collections.singletonList(new PedidoRequest.ItemRequest("1", "Item 1", new BigDecimal("100.00"), 1)))
                .destinatario(destinatario)
                .build();

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(pedido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(containsString("destinatario.enderecos:")));
    }
}
