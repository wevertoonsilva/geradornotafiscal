package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.application.port.in.GerarNotaFiscalPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    private static final String DESTINATARIO_FISICA = """
            {
              "nome": "Pessoa Fisica",
              "tipo_pessoa": "FISICA",
              "documento": {"tipo": "CPF", "numero": "123.456.789-00"},
              "enderecos": [{"logradouro": "Rua A", "numero": "1", "cidade": "SP", "estado": "SP", "cep": "01310-100", "regiao": "SUDESTE", "finalidade": "ENTREGA"}]
            }
            """;

    private static final String DESTINATARIO_JURIDICA_SEM_REGIME = """
            {
              "nome": "Empresa LTDA",
              "tipo_pessoa": "JURIDICA",
              "documento": {"tipo": "CNPJ", "numero": "12.345.678/0001-90"},
              "enderecos": [{"logradouro": "Av B", "numero": "2", "cidade": "SP", "estado": "SP", "cep": "01310-100", "regiao": "SUDESTE", "finalidade": "ENTREGA"}]
            }
            """;

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
                    "documento": {"tipo": "CPF", "numero": "123.456.789-00"},
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
