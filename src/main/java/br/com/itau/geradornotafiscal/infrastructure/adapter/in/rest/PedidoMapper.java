package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.Destinatario;
import br.com.itau.geradornotafiscal.domain.model.Documento;
import br.com.itau.geradornotafiscal.domain.model.Endereco;
import br.com.itau.geradornotafiscal.domain.model.Item;
import br.com.itau.geradornotafiscal.domain.model.Pedido;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DocumentoRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.EnderecoRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.ItemRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.PedidoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoMapper {

    @Mapping(source = "idPedido", target = "idPedido")
    @Mapping(source = "valorTotalItens", target = "valorTotalItens")
    @Mapping(source = "valorFrete", target = "valorFrete")
    @Mapping(source = "itens", target = "itens")
    @Mapping(source = "destinatario", target = "destinatario")
    Pedido toDomain(PedidoRequest request);

    Item toDomain(ItemRequest item);

    Destinatario toDomain(DestinatarioRequest destinatario);

    Documento toDomain(DocumentoRequest documento);

    Endereco toDomain(EnderecoRequest endereco);
}
