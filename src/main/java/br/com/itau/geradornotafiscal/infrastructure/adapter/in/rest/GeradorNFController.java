package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.application.port.in.GerarNotaFiscalPort;
import br.com.itau.geradornotafiscal.domain.model.Pedido;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.V1Api;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.NotaFiscal;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.PedidoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GeradorNFController implements V1Api {

    private final GerarNotaFiscalPort gerarNotaFiscalPort;
    private final PedidoMapper pedidoMapper;
    private final NotaFiscalMapper notaFiscalMapper;

    public GeradorNFController(
            GerarNotaFiscalPort gerarNotaFiscalPort,
            PedidoMapper pedidoMapper,
            NotaFiscalMapper notaFiscalMapper) {
        this.gerarNotaFiscalPort = gerarNotaFiscalPort;
        this.pedidoMapper = pedidoMapper;
        this.notaFiscalMapper = notaFiscalMapper;
    }

    @Override
    public ResponseEntity<NotaFiscal> gerarNotaFiscal(PedidoRequest pedidoRequest) {
        Pedido pedido = pedidoMapper.toDomain(pedidoRequest);
        br.com.itau.geradornotafiscal.domain.model.NotaFiscal notaFiscalDomain = gerarNotaFiscalPort.gerarNotaFiscal(pedido);
        NotaFiscal response = notaFiscalMapper.toResponse(notaFiscalDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
