package br.com.itau.geradornotafiscal.domain.policy;

import br.com.itau.geradornotafiscal.domain.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;
import br.com.itau.geradornotafiscal.domain.model.Pedido;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class NotaFiscalFactory {

    public NotaFiscal criar(Pedido pedido, List<ItemNotaFiscal> itensComTributo, BigDecimal freteCalculado) {
        String idNotaFiscal = UUID.randomUUID().toString();

        return NotaFiscal.builder()
                .idNotaFiscal(idNotaFiscal)
                .idPedido(pedido.getIdPedido())
                .data(LocalDateTime.now())
                .valorTotalItens(pedido.getValorTotalItens())
                .valorFrete(freteCalculado)
                .itens(itensComTributo)
                .destinatario(pedido.getDestinatario())
                .build();
    }
}
