package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.application.port.in.GerarNotaFiscalPort;
import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;
import br.com.itau.geradornotafiscal.domain.model.Pedido;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private static final String DESTINATARIO_FISICA = """
            {
              "nome": "Pessoa Fisica",
              "tipo_pessoa": "FISICA",
              "documentos": [{"tipo": "CPF", "numero": "123.456.789-00"}],
              "enderecos": [{"logradouro": "Rua A", "numero": "1", "cidade": "SP", "estado": "SP", "cep": "01310-100", "regiao": "SUDESTE", "finalidade": "ENTREGA"}]
            }
            """;

    private static final String DESTINATARIO_JURIDICA_SEM_REGIME = """
            {
              "nome": "Empresa LTDA",
              "tipo_pessoa": "JURIDICA",
              "documentos": [{"tipo": "CNPJ", "numero": "12.345.678/0001-90"}],
              "enderecos": [{"logradouro": "Av B", "numero": "2", "cidade": "SP", "estado": "SP", "cep": "01310-100", "regiao": "SUDESTE", "finalidade": "ENTREGA"}]
            }
            """;

    @Test
    void deveAceitarPayloadPessoaFisicaDoSwagger() throws Exception {
        when(notaFiscalService.gerarNotaFiscal(any(Pedido.class))).thenReturn(NotaFiscal.builder()
                .idNotaFiscal(UUID.randomUUID().toString())
                .idPedido(1)
                .data(LocalDateTime.of(2022, 5, 1, 0, 0))
                .valorTotalItens(new BigDecimal("100.0"))
                .valorFrete(new BigDecimal("10.48"))
                .itens(List.of())
                .build());

        String payload = """
                {
                  "id_pedido": 1,
                  "data": "2022-05-01",
                  "valor_total_itens": 100.0,
                  "valor_frete": 10.0,
                  "itens": [
                    {
                      "id_item": "1",
                      "descricao": "Teclado USB",
                      "valor_unitario": 50,
                      "quantidade": 2
                    }
                  ],
                  "destinatario": {
                    "nome": "John Doe",
                    "tipo_pessoa": "FISICA",
                    "documentos": [
                      {
                        "tipo": "CPF",
                        "numero": "88740347095"
                      }
                    ],
                    "enderecos": [
                      {
                        "logradouro": "Av do estado",
                        "numero": "5533",
                        "complemento": "4 anndar b",
                        "bairro": "Mooca",
                        "cidade": "São Paulo",
                        "estado": "SP",
                        "pais": "Brasil",
                        "cep": "03105003",
                        "finalidade": "ENTREGA",
                        "regiao": "SUDESTE"
                      }
                    ]
                  }
                }
                """;

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(notaFiscalService).gerarNotaFiscal(pedidoCaptor.capture());
        Pedido pedido = pedidoCaptor.getValue();
        assertEquals("1", pedido.getItens().getFirst().getIdItem());
        assertEquals("88740347095", pedido.getDestinatario().getDocumentos().getFirst().getNumero());
        assertEquals("Mooca", pedido.getDestinatario().getEnderecos().getFirst().getBairro());
        assertEquals("Brasil", pedido.getDestinatario().getEnderecos().getFirst().getPais());
    }

    @Test
    void deveRetornar400QuandoPedidoSemDestinatario() throws Exception {
        String payload = """
                {
                  "id_pedido": 1,
                  "data": "2026-05-15",
                  "valor_total_itens": 100.00,
                  "valor_frete": 10.00,
                  "itens": [{"id_item": "1", "descricao": "Item 1", "valor_unitario": 100.00, "quantidade": 1}]
                }
                """;

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição Inválida"))
                .andExpect(jsonPath("$.errors").value(containsString("destinatario:")));
    }

    @Test
    void deveRetornar400QuandoEnumInvalido() throws Exception {
        String payload = """
                {
                  "id_pedido": 1,
                  "data": "2026-05-15",
                  "valor_total_itens": 100.00,
                  "valor_frete": 10.00,
                  "itens": [{"id_item": "1", "descricao": "Item 1", "valor_unitario": 100.00, "quantidade": 1}],
                  "destinatario": {
                    "nome": "Pessoa Fisica",
                    "tipo_pessoa": "PF",
                    "documentos": [{"tipo": "CPF", "numero": "123.456.789-00"}],
                    "enderecos": [{"logradouro": "Rua A", "numero": "1", "cidade": "SP", "estado": "SP", "cep": "01310-100", "regiao": "SUDESTE", "finalidade": "ENTREGA"}]
                  }
                }
                """;

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição Inválida"))
                .andExpect(jsonPath("$.detail").value("Payload JSON inválido ou incompatível com o contrato"))
                .andExpect(jsonPath("$.errors").value(containsString("Unexpected value 'PF'")));
    }

    @Test
    void deveRetornar400QuandoJuridicaSemRegimeTributacao() throws Exception {
        String payload = String.format("""
                {
                  "id_pedido": 1,
                  "data": "2026-05-15",
                  "valor_total_itens": 100.00,
                  "valor_frete": 10.00,
                  "itens": [{"id_item": "1", "descricao": "Item 1", "valor_unitario": 100.00, "quantidade": 1}],
                  "destinatario": %s
                }
                """, DESTINATARIO_JURIDICA_SEM_REGIME);

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(containsString("destinatario.regimeTributacao: JURIDICA deve ter regime de tributação informado")));
    }

    @Test
    void deveRetornar400QuandoTotaisNaoReconciliam() throws Exception {
        String payload = String.format("""
                {
                  "id_pedido": 1,
                  "data": "2026-05-15",
                  "valor_total_itens": 100.00,
                  "valor_frete": 10.00,
                  "itens": [{"id_item": "1", "descricao": "Item 1", "valor_unitario": 50.00, "quantidade": 1}],
                  "destinatario": %s
                }
                """, DESTINATARIO_FISICA);

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(containsString("O valor total dos itens não corresponde à soma dos itens")));
    }

    @Test
    void deveRetornar400QuandoSemEnderecos() throws Exception {
        String payload = """
                {
                  "id_pedido": 1,
                  "data": "2026-05-15",
                  "valor_total_itens": 100.00,
                  "valor_frete": 10.00,
                  "itens": [{"id_item": "1", "descricao": "Item 1", "valor_unitario": 100.00, "quantidade": 1}],
                  "destinatario": {
                    "nome": "Pessoa Fisica",
                    "tipo_pessoa": "FISICA",
                    "documentos": [{"tipo": "CPF", "numero": "123.456.789-00"}],
                    "enderecos": []
                  }
                }
                """;

        mockMvc.perform(post("/v1/notas-fiscais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(containsString("destinatario.enderecos:")));
    }
}
