package br.com.itau.geradornotafiscal.model;

import br.com.itau.geradornotafiscal.validator.ReconciliacaoTotais;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ReconciliacaoTotais
public class Pedido {
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
    private List<Item> itens;

    @JsonProperty("destinatario")
    @NotNull
    @Valid
    private Destinatario destinatario;

}
