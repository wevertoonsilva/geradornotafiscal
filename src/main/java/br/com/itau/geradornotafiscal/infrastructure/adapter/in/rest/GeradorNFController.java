package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.application.port.in.GerarNotaFiscalPort;
import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notas-fiscais")
public class GeradorNFController {

    private final GerarNotaFiscalPort notaFiscalService;

    public GeradorNFController(GerarNotaFiscalPort notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @PostMapping
    public ResponseEntity<NotaFiscal> gerarNotaFiscal(
            @Valid @RequestBody PedidoRequest pedidoRequest) {
        NotaFiscal notaFiscal = notaFiscalService.gerarNotaFiscal(pedidoRequest.toDomain());
        return ResponseEntity.ok(notaFiscal);
    }
}
