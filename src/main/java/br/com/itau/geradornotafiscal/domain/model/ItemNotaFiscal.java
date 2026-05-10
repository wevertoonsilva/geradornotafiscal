package br.com.itau.geradornotafiscal.domain.model;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemNotaFiscal {
    private String idItem;
    private String descricao;
    private BigDecimal valorUnitario;
    private int quantidade;
    private BigDecimal valorTributoItem;
}
