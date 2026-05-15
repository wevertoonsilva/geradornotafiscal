package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.Destinatario;
import br.com.itau.geradornotafiscal.domain.model.Documento;
import br.com.itau.geradornotafiscal.domain.model.Endereco;
import br.com.itau.geradornotafiscal.domain.model.Finalidade;
import br.com.itau.geradornotafiscal.domain.model.Regiao;
import br.com.itau.geradornotafiscal.domain.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.domain.model.TipoDocumento;
import br.com.itau.geradornotafiscal.domain.model.TipoPessoa;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DocumentoRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.EnderecoRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.NotaFiscal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotaFiscalMapper {

    @Mapping(source = "idNotaFiscal", target = "idNotaFiscal", qualifiedByName = "stringToUUID")
    @Mapping(source = "data", target = "data", qualifiedByName = "localDateTimeToOffsetDateTime")
    NotaFiscal toResponse(br.com.itau.geradornotafiscal.domain.model.NotaFiscal domain);

    @Mapping(source = "tipoPessoa", target = "tipoPessoa", qualifiedByName = "tipoPessoaToTipoPessoaEnum")
    @Mapping(source = "regimeTributacao", target = "regimeTributacao", qualifiedByName = "regimeTributacaoPJToRegimeTributacaoEnum")
    @Mapping(source = "documentos", target = "documento", qualifiedByName = "documentosToDocumento")
    DestinatarioRequest toResponse(Destinatario domain);

    @Mapping(source = "tipo", target = "tipo", qualifiedByName = "tipoDocumentoToTipoEnum")
    DocumentoRequest toResponse(Documento domain);

    @Mapping(source = "regiao", target = "regiao", qualifiedByName = "regiaoToRegiaoEnum")
    @Mapping(source = "finalidade", target = "finalidade", qualifiedByName = "finalidadeToFinalidadeEnum")
    EnderecoRequest toResponse(Endereco domain);

    br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.ItemNotaFiscal toResponse(
            br.com.itau.geradornotafiscal.domain.model.ItemNotaFiscal domain);

    @Named("stringToUUID")
    default UUID stringToUUID(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atOffset(ZoneOffset.UTC) : null;
    }

    @Named("tipoPessoaToTipoPessoaEnum")
    default DestinatarioRequest.TipoPessoaEnum tipoPessoaToTipoPessoaEnum(TipoPessoa tipoPessoa) {
        if (tipoPessoa == null) return null;
        return DestinatarioRequest.TipoPessoaEnum.valueOf(tipoPessoa.name());
    }

    @Named("regimeTributacaoPJToRegimeTributacaoEnum")
    default DestinatarioRequest.RegimeTributacaoEnum regimeTributacaoPJToRegimeTributacaoEnum(RegimeTributacaoPJ regimeTributacao) {
        if (regimeTributacao == null) return null;
        return DestinatarioRequest.RegimeTributacaoEnum.valueOf(regimeTributacao.name());
    }

    @Named("documentosToDocumento")
    default DocumentoRequest documentosToDocumento(List<Documento> documentos) {
        if (documentos == null || documentos.isEmpty()) return null;
        return toResponse(documentos.getFirst());
    }

    @Named("tipoDocumentoToTipoEnum")
    default DocumentoRequest.TipoEnum tipoDocumentoToTipoEnum(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) return null;
        return DocumentoRequest.TipoEnum.valueOf(tipoDocumento.name());
    }

    @Named("regiaoToRegiaoEnum")
    default EnderecoRequest.RegiaoEnum regiaoToRegiaoEnum(Regiao regiao) {
        if (regiao == null) return null;
        return EnderecoRequest.RegiaoEnum.valueOf(regiao.name());
    }

    @Named("finalidadeToFinalidadeEnum")
    default EnderecoRequest.FinalidadeEnum finalidadeToFinalidadeEnum(Finalidade finalidade) {
        if (finalidade == null) return null;
        return EnderecoRequest.FinalidadeEnum.valueOf(finalidade.name());
    }
}
