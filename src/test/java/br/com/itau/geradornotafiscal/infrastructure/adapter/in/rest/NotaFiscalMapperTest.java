package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.Destinatario;
import br.com.itau.geradornotafiscal.domain.model.Documento;
import br.com.itau.geradornotafiscal.domain.model.Endereco;
import br.com.itau.geradornotafiscal.domain.model.Finalidade;
import br.com.itau.geradornotafiscal.domain.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.domain.model.Regiao;
import br.com.itau.geradornotafiscal.domain.model.TipoDocumento;
import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.EnderecoRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotaFiscalMapperTest {

    private final NotaFiscalMapper mapper = Mappers.getMapper(NotaFiscalMapper.class);

    @Test
    void deveMapearNotaFiscalCompletaParaResponse() {
        String idNotaFiscal = UUID.randomUUID().toString();
        br.com.itau.geradornotafiscal.domain.model.NotaFiscal domain =
                br.com.itau.geradornotafiscal.domain.model.NotaFiscal.builder()
                        .idNotaFiscal(idNotaFiscal)
                        .idPedido(1)
                        .data(LocalDateTime.of(2026, 5, 15, 12, 57, 59))
                        .valorTotalItens(new BigDecimal("150"))
                        .valorFrete(new BigDecimal("21.60"))
                        .itens(List.of(ItemNotaFiscal.builder()
                                .idItem("PROD-001")
                                .descricao("Produto Teste")
                                .quantidade(2)
                                .valorUnitario(new BigDecimal("75"))
                                .valorTributoItem(new BigDecimal("18.00"))
                                .build()))
                        .destinatario(Destinatario.builder()
                                .nome("Joao Silva")
                                .tipoPessoa(TipoPessoa.FISICA)
                                .documentos(List.of(new Documento("123.456.789-00", TipoDocumento.CPF)))
                                .enderecos(List.of(Endereco.builder()
                                        .logradouro("Rua A")
                                        .numero("1")
                                        .complemento("Apto 10")
                                        .bairro("Centro")
                                        .cidade("Sao Paulo")
                                        .estado("SP")
                                        .pais("Brasil")
                                        .cep("01310-100")
                                        .regiao(Regiao.SUDESTE)
                                        .finalidade(Finalidade.ENTREGA)
                                        .build()))
                                .build())
                        .build();

        br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.NotaFiscal response =
                mapper.toResponse(domain);

        assertEquals(UUID.fromString(idNotaFiscal), response.getIdNotaFiscal());
        assertEquals(1, response.getIdPedido());
        assertEquals(new BigDecimal("150"), response.getValorTotalItens());
        assertEquals(new BigDecimal("21.60"), response.getValorFrete());
        assertEquals(1, response.getItens().size());
        assertEquals("PROD-001", response.getItens().getFirst().getIdItem());
        assertEquals("Produto Teste", response.getItens().getFirst().getDescricao());
        assertEquals(new BigDecimal("18.00"), response.getItens().getFirst().getValorTributoItem());
        assertEquals("Joao Silva", response.getDestinatario().getNome());
        assertEquals(DestinatarioRequest.TipoPessoaEnum.FISICA, response.getDestinatario().getTipoPessoa());
        assertEquals(1, response.getDestinatario().getDocumentos().size());
        assertEquals("123.456.789-00", response.getDestinatario().getDocumentos().getFirst().getNumero());
        assertEquals(1, response.getDestinatario().getEnderecos().size());
        assertEquals("Sao Paulo", response.getDestinatario().getEnderecos().getFirst().getCidade());
        assertEquals("Apto 10", response.getDestinatario().getEnderecos().getFirst().getComplemento());
        assertEquals("Centro", response.getDestinatario().getEnderecos().getFirst().getBairro());
        assertEquals("Brasil", response.getDestinatario().getEnderecos().getFirst().getPais());
    }

    @Test
    void deveMapearTodasAsFinalidadesDoDominioParaResponse() {
        for (Finalidade finalidade : Finalidade.values()) {
            Endereco endereco = Endereco.builder()
                    .finalidade(finalidade)
                    .build();

            EnderecoRequest response = mapper.toResponse(endereco);

            assertEquals(EnderecoRequest.FinalidadeEnum.valueOf(finalidade.name()), response.getFinalidade());
        }
    }
}
