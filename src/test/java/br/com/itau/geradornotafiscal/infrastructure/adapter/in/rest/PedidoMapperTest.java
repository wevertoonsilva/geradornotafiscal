package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PedidoMapperTest {

    private final PedidoMapper mapper = Mappers.getMapper(PedidoMapper.class);

    @Test
    void deveMapearTipoPessoaFisica() {
        TipoPessoa tipoPessoa = mapper.tipoPessoaEnumToTipoPessoa(DestinatarioRequest.TipoPessoaEnum.FISICA);

        assertEquals(TipoPessoa.FISICA, tipoPessoa);
    }

    @Test
    void deveMapearTipoPessoaJuridica() {
        TipoPessoa tipoPessoa = mapper.tipoPessoaEnumToTipoPessoa(DestinatarioRequest.TipoPessoaEnum.JURIDICA);

        assertEquals(TipoPessoa.JURIDICA, tipoPessoa);
    }

    @Test
    void deveManterTipoPessoaNulo() {
        TipoPessoa tipoPessoa = mapper.tipoPessoaEnumToTipoPessoa(null);

        assertNull(tipoPessoa);
    }
}
