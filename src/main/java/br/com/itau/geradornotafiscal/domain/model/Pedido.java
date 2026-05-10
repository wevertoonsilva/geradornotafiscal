package br.com.itau.geradornotafiscal.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Pedido {
    private int idPedido;
    private LocalDate data;
    private BigDecimal valorTotalItens;
    private BigDecimal valorFrete;
    private List<Item> itens;
    private Destinatario destinatario;
}
