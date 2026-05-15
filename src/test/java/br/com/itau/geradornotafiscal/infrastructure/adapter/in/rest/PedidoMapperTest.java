package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.Destinatario;
import br.com.itau.geradornotafiscal.domain.model.Finalidade;
import br.com.itau.geradornotafiscal.domain.model.Regiao;
import br.com.itau.geradornotafiscal.domain.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.domain.model.TipoDocumento;
import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DocumentoRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.EnderecoRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoMapperTest {

    private final PedidoMapper mapper = Mappers.getMapper(PedidoMapper.class);

    @Test
    void deveMapearTipoPessoaFisica() {
        DestinatarioRequest request = destinatarioRequest(DestinatarioRequest.TipoPessoaEnum.FISICA, null);

        Destinatario destinatario = mapper.toDomain(request);

        assertEquals(TipoPessoa.FISICA, destinatario.getTipoPessoa());
    }

    @Test
    void deveMapearTipoPessoaJuridica() {
        DestinatarioRequest request = destinatarioRequest(
                DestinatarioRequest.TipoPessoaEnum.JURIDICA,
                DestinatarioRequest.RegimeTributacaoEnum.SIMPLES_NACIONAL);

        Destinatario destinatario = mapper.toDomain(request);

        assertEquals(TipoPessoa.JURIDICA, destinatario.getTipoPessoa());
        assertEquals(RegimeTributacaoPJ.SIMPLES_NACIONAL, destinatario.getRegimeTributacao());
    }

    @Test
    void deveMapearRegimeTributacaoOutros() {
        DestinatarioRequest request = destinatarioRequest(
                DestinatarioRequest.TipoPessoaEnum.JURIDICA,
                DestinatarioRequest.RegimeTributacaoEnum.OUTROS);

        Destinatario destinatario = mapper.toDomain(request);

        assertEquals(RegimeTributacaoPJ.OUTROS, destinatario.getRegimeTributacao());
    }

    @Test
    void deveMapearDocumentosEEndereco() {
        DestinatarioRequest request = destinatarioRequest(DestinatarioRequest.TipoPessoaEnum.FISICA, null);
        EnderecoRequest endereco = new EnderecoRequest();
        endereco.setLogradouro("Av do estado");
        endereco.setNumero("5533");
        endereco.setComplemento("4 andar b");
        endereco.setBairro("Mooca");
        endereco.setCidade("Sao Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");
        endereco.setCep("03105003");
        endereco.setRegiao(EnderecoRequest.RegiaoEnum.SUDESTE);
        endereco.setFinalidade(EnderecoRequest.FinalidadeEnum.ENTREGA);
        request.setEnderecos(List.of(endereco));

        Destinatario destinatario = mapper.toDomain(request);

        assertEquals(1, destinatario.getDocumentos().size());
        assertEquals(TipoDocumento.CPF, destinatario.getDocumentos().getFirst().getTipo());
        assertEquals("123.456.789-00", destinatario.getDocumentos().getFirst().getNumero());
        assertEquals("4 andar b", destinatario.getEnderecos().getFirst().getComplemento());
        assertEquals("Mooca", destinatario.getEnderecos().getFirst().getBairro());
        assertEquals("Brasil", destinatario.getEnderecos().getFirst().getPais());
        assertEquals(Regiao.SUDESTE, destinatario.getEnderecos().getFirst().getRegiao());
        assertEquals(Finalidade.ENTREGA, destinatario.getEnderecos().getFirst().getFinalidade());
    }

    private DestinatarioRequest destinatarioRequest(
            DestinatarioRequest.TipoPessoaEnum tipoPessoa,
            DestinatarioRequest.RegimeTributacaoEnum regimeTributacao) {
        DestinatarioRequest request = new DestinatarioRequest();
        request.setTipoPessoa(tipoPessoa);
        request.setRegimeTributacao(regimeTributacao);
        request.setDocumentos(List.of(new DocumentoRequest(DocumentoRequest.TipoEnum.CPF, "123.456.789-00")));
        return request;
    }
}
