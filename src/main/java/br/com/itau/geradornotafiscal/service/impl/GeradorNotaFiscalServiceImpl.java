package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.port.out.EntregaPort;
import br.com.itau.geradornotafiscal.port.out.EstoquePort;
import br.com.itau.geradornotafiscal.port.out.FinanceiroPort;
import br.com.itau.geradornotafiscal.port.out.RegistroPort;
import br.com.itau.geradornotafiscal.service.AliquotaStrategy;
import br.com.itau.geradornotafiscal.service.AliquotaStrategyFactory;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService{

	private final CalculadoraAliquotaProduto calculadoraAliquotaProduto;

	private final EstoquePort estoquePort;
	private final RegistroPort registroPort;
	private final EntregaPort entregaPort;
	private final FinanceiroPort financeiroPort;

	public GeradorNotaFiscalServiceImpl(
			CalculadoraAliquotaProduto calculadoraAliquotaProduto,
			EstoquePort estoquePort,
			RegistroPort registroPort,
			EntregaPort entregaPort,
			FinanceiroPort financeiroPort) {
		this.calculadoraAliquotaProduto = calculadoraAliquotaProduto;
		this.estoquePort = estoquePort;
		this.registroPort = registroPort;
		this.entregaPort = entregaPort;
		this.financeiroPort = financeiroPort;
	}

	@Override
	public NotaFiscal gerarNotaFiscal(Pedido pedido) {

		Destinatario destinatario = pedido.getDestinatario();
		TipoPessoa tipoPessoa = destinatario.getTipoPessoa();
		RegimeTributacaoPJ regimeTributacao = destinatario.getRegimeTributacao();

		AliquotaStrategy strategy = AliquotaStrategyFactory.getStrategy(tipoPessoa, regimeTributacao);
		double aliquota = strategy.calcularAliquota(pedido.getValorTotalItens());

		List<ItemNotaFiscal> itemNotaFiscalList = calculadoraAliquotaProduto.calcularAliquota(pedido.getItens(), aliquota);
		//Regras diferentes para frete

		Regiao regiao = destinatario.getEnderecos().stream()
				.filter(endereco -> endereco.getFinalidade() == Finalidade.ENTREGA || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA)
				.map(Endereco::getRegiao)
				.findFirst()
				.orElse(null);

		double valorFrete = pedido.getValorFrete();
		double valorFreteComPercentual =0;

		if (regiao == Regiao.NORTE) {
			valorFreteComPercentual = valorFrete * 1.08;
		} else if (regiao == Regiao.NORDESTE) {
			valorFreteComPercentual = valorFrete * 1.085;
		} else if (regiao == Regiao.CENTRO_OESTE) {
			valorFreteComPercentual = valorFrete * 1.07;
		} else if (regiao == Regiao.SUDESTE) {
			valorFreteComPercentual = valorFrete * 1.048;
		} else if (regiao == Regiao.SUL) {
			valorFreteComPercentual = valorFrete * 1.06;
		}

		// Create the NotaFiscal object
		String idNotaFiscal = UUID.randomUUID().toString();

		NotaFiscal notaFiscal = NotaFiscal.builder()
				.idNotaFiscal(idNotaFiscal)
				.data(LocalDateTime.now())
				.valorTotalItens(pedido.getValorTotalItens())
				.valorFrete(valorFreteComPercentual)
				.itens(itemNotaFiscalList)
				.destinatario(pedido.getDestinatario())
				.build();

		estoquePort.enviarNotaFiscalParaBaixaEstoque(notaFiscal);
		registroPort.registrarNotaFiscal(notaFiscal);
		entregaPort.agendarEntrega(notaFiscal);
		financeiroPort.enviarNotaFiscalParaContasReceber(notaFiscal);

		return notaFiscal;
	}
}