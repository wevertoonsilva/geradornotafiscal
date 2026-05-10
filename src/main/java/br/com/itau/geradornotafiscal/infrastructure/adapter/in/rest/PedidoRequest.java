package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ReconciliacaoTotais
public class PedidoRequest {
    @JsonProperty("id_pedido")
    private int idPedido;

    @JsonProperty("data")
    private LocalDate data;

    @JsonProperty("valor_total_itens")
    @Positive
    private BigDecimal valorTotalItens;

    @JsonProperty("valor_frete")
    @Positive
    private BigDecimal valorFrete;

    @JsonProperty("itens")
    @NotNull
    @NotEmpty
    @Valid
    private List<ItemRequest> itens;

    @JsonProperty("destinatario")
    @NotNull
    @Valid
    private DestinatarioRequest destinatario;

    public Pedido toDomain() {
        return Pedido.builder()
                .idPedido(this.idPedido)
                .data(this.data)
                .valorTotalItens(this.valorTotalItens)
                .valorFrete(this.valorFrete)
                .itens(this.itens.stream().map(ItemRequest::toDomain).collect(Collectors.toList()))
                .destinatario(this.destinatario.toDomain())
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemRequest {
        @JsonProperty("id_item")
        private String idItem;

        @JsonProperty("descricao")
        private String descricao;

        @JsonProperty("valor_unitario")
        private BigDecimal valorUnitario;

        @JsonProperty("quantidade")
        private int quantidade;

        public Item toDomain() {
            return new Item(idItem, descricao, valorUnitario, quantidade);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DestinatarioRequest {
        @JsonProperty("nome")
        private String nome;

        @JsonProperty("tipo_pessoa")
        @NotNull
        private TipoPessoa tipoPessoa;

        @JsonProperty("regime_tributacao")
        private RegimeTributacaoPJ regimeTributacao;

        @JsonProperty("documentos")
        private List<DocumentoRequest> documentos;

        @JsonProperty("enderecos")
        @NotNull
        @NotEmpty
        private List<EnderecoRequest> enderecos;

        public Destinatario toDomain() {
            return Destinatario.builder()
                    .nome(this.nome)
                    .tipoPessoa(this.tipoPessoa)
                    .regimeTributacao(this.regimeTributacao)
                    .documentos(this.documentos != null ? this.documentos.stream().map(DocumentoRequest::toDomain).collect(Collectors.toList()) : null)
                    .enderecos(this.enderecos.stream().map(EnderecoRequest::toDomain).collect(Collectors.toList()))
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentoRequest {
        @JsonProperty("numero")
        private String numero;
        @JsonProperty("tipo")
        private TipoDocumento tipo;

        public Documento toDomain() {
            return new Documento(numero, tipo);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnderecoRequest {
        @JsonProperty("cep")
        private String cep;

        @JsonProperty("logradouro")
        private String logradouro;

        @JsonProperty("numero")
        private String numero;

        @JsonProperty("estado")
        private String estado;

        @JsonProperty("complemento")
        private String complemento;

        @JsonProperty("finalidade")
        private Finalidade finalidade;

        @JsonProperty("regiao")
        private Regiao regiao;

        public Endereco toDomain() {
            return Endereco.builder()
                    .cep(this.cep)
                    .logradouro(this.logradouro)
                    .numero(this.numero)
                    .estado(this.estado)
                    .complemento(this.complemento)
                    .finalidade(this.finalidade)
                    .regiao(this.regiao)
                    .build();
        }
    }
}
