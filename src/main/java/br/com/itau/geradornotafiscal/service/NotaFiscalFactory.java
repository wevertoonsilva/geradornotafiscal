package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.model.Pedido;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class NotaFiscalFactory {

    public NotaFiscal criar(Pedido pedido, List<ItemNotaFiscal> itensComTributo, double freteCalculado) {
        String idNotaFiscal = UUID.randomUUID().toString();

        return NotaFiscal.builder()
                .idNotaFiscal(idNotaFiscal)
                .data(LocalDateTime.now())
                .valorTotalItens(pedido.getValorTotalItens())
                .valorFrete(freteCalculado)
                .itens(itensComTributo)
                .destinatario(pedido.getDestinatario())
                .build();
    }
}
