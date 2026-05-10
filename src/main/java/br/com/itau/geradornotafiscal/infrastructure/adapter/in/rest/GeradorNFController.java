package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;
import jakarta.validation.Valid;

import br.com.itau.geradornotafiscal.domain.model.Pedido;
import br.com.itau.geradornotafiscal.application.port.in.GerarNotaFiscalPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedido")
public class GeradorNFController {

	private final GerarNotaFiscalPort notaFiscalService;

	public GeradorNFController(GerarNotaFiscalPort notaFiscalService) {
		this.notaFiscalService = notaFiscalService;
	}

	@PostMapping("/gerarNotaFiscal")
	public ResponseEntity<NotaFiscal> gerarNotaFiscal(@Valid @RequestBody PedidoRequest pedidoRequest) {
		// Lógica de processamento do pedido
		// Aqui você pode realizar as operações desejadas com o objeto Pedido

		// Exemplo de retorno
		String mensagem = "Nota fiscal gerada com sucesso para o pedido: " + pedidoRequest.getIdPedido();
		NotaFiscal notaFiscal = notaFiscalService.gerarNotaFiscal(pedidoRequest.toDomain());
		return new ResponseEntity<>(notaFiscal, HttpStatus.OK);
	}
	
}
