package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.NotaFiscal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotaFiscalMapper {

    @Mapping(source = "idNotaFiscal", target = "idNotaFiscal", qualifiedByName = "stringToUUID")
    @Mapping(source = "data", target = "data", qualifiedByName = "localDateTimeToOffsetDateTime")
    NotaFiscal toResponse(br.com.itau.geradornotafiscal.domain.model.NotaFiscal domain);

    @Named("stringToUUID")
    default UUID stringToUUID(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atOffset(ZoneOffset.UTC) : null;
    }
}
