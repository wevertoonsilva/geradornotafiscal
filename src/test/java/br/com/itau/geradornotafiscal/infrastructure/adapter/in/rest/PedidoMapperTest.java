package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.Destinatario;
import br.com.itau.geradornotafiscal.domain.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DocumentoRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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

    private DestinatarioRequest destinatarioRequest(
            DestinatarioRequest.TipoPessoaEnum tipoPessoa,
            DestinatarioRequest.RegimeTributacaoEnum regimeTributacao) {
        DestinatarioRequest request = new DestinatarioRequest();
        request.setTipoPessoa(tipoPessoa);
        request.setRegimeTributacao(regimeTributacao);
        request.setDocumento(new DocumentoRequest(DocumentoRequest.TipoEnum.CPF, "123.456.789-00"));
        return request;
    }
}
