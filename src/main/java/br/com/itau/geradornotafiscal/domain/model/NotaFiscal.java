package br.com.itau.geradornotafiscal.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscal {
    private String idNotaFiscal;
    private int idPedido;
    private LocalDateTime data;
    private BigDecimal valorTotalItens;
    private BigDecimal valorFrete;
    private List<ItemNotaFiscal> itens;
    private Destinatario destinatario;
}
