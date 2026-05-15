package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.Destinatario;
import br.com.itau.geradornotafiscal.domain.model.Documento;
import br.com.itau.geradornotafiscal.domain.model.Endereco;
import br.com.itau.geradornotafiscal.domain.model.Finalidade;
import br.com.itau.geradornotafiscal.domain.model.Item;
import br.com.itau.geradornotafiscal.domain.model.Pedido;
import br.com.itau.geradornotafiscal.domain.model.Regiao;
import br.com.itau.geradornotafiscal.domain.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.domain.model.TipoDocumento;
import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DocumentoRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.EnderecoRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.ItemRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.PedidoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoMapper {

    @Mapping(source = "idPedido", target = "idPedido")
    @Mapping(source = "valorTotalItens", target = "valorTotalItens")
    @Mapping(source = "valorFrete", target = "valorFrete")
    @Mapping(source = "itens", target = "itens")
    @Mapping(source = "destinatario", target = "destinatario")
    Pedido toDomain(PedidoRequest request);

    Item toDomain(ItemRequest item);

    @Mapping(source = "tipoPessoa", target = "tipoPessoa", qualifiedByName = "tipoPessoaEnumToTipoPessoa")
    @Mapping(source = "regimeTributacao", target = "regimeTributacao", qualifiedByName = "regimeTributacaoEnumToRegimeTributacaoPJ")
    @Mapping(source = "documento", target = "documentos", qualifiedByName = "documentoToList")
    Destinatario toDomain(DestinatarioRequest destinatario);

    @Mapping(source = "tipo", target = "tipo", qualifiedByName = "documentoTipoEnumToTipoDocumento")
    Documento toDomain(DocumentoRequest documento);

    @Mapping(source = "regiao", target = "regiao", qualifiedByName = "regiaoEnumToRegiao")
    @Mapping(source = "finalidade", target = "finalidade", qualifiedByName = "finalidadeEnumToFinalidade")
    Endereco toDomain(EnderecoRequest endereco);

    @Named("tipoPessoaEnumToTipoPessoa")
    default TipoPessoa tipoPessoaEnumToTipoPessoa(DestinatarioRequest.TipoPessoaEnum tipoPessoaEnum) {
        if (tipoPessoaEnum == null) return null;
        return switch (tipoPessoaEnum) {
            case PF -> TipoPessoa.FISICA;
            case PJ -> TipoPessoa.JURIDICA;
        };
    }

    @Named("regimeTributacaoEnumToRegimeTributacaoPJ")
    default RegimeTributacaoPJ regimeTributacaoEnumToRegimeTributacaoPJ(DestinatarioRequest.RegimeTributacaoEnum regimeTributacaoEnum) {
        if (regimeTributacaoEnum == null) return null;
        return RegimeTributacaoPJ.valueOf(regimeTributacaoEnum.getValue());
    }

    @Named("documentoToList")
    default List<Documento> documentoToList(DocumentoRequest documento) {
        if (documento == null) return Collections.emptyList();
        return List.of(toDomain(documento));
    }

    @Named("documentoTipoEnumToTipoDocumento")
    default TipoDocumento documentoTipoEnumToTipoDocumento(DocumentoRequest.TipoEnum tipoEnum) {
        if (tipoEnum == null) return null;
        return TipoDocumento.valueOf(tipoEnum.getValue());
    }

    @Named("regiaoEnumToRegiao")
    default Regiao regiaoEnumToRegiao(EnderecoRequest.RegiaoEnum regiaoEnum) {
        if (regiaoEnum == null) return null;
        return Regiao.valueOf(regiaoEnum.getValue());
    }

    @Named("finalidadeEnumToFinalidade")
    default Finalidade finalidadeEnumToFinalidade(EnderecoRequest.FinalidadeEnum finalidadeEnum) {
        if (finalidadeEnum == null) return null;
        return Finalidade.valueOf(finalidadeEnum.getValue());
    }
}
