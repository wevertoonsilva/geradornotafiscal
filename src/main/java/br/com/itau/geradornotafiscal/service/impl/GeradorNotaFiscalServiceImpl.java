package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.port.out.EntregaPort;
import br.com.itau.geradornotafiscal.port.out.EstoquePort;
import br.com.itau.geradornotafiscal.port.out.FinanceiroPort;
import br.com.itau.geradornotafiscal.port.out.RegistroPort;
import br.com.itau.geradornotafiscal.service.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService{

	private final CalculadoraAliquotaProduto calculadoraAliquotaProduto;
	private final CalculadoraFrete calculadoraFrete;
	private final NotaFiscalFactory notaFiscalFactory;

	private final EstoquePort estoquePort;
	private final RegistroPort registroPort;
	private final EntregaPort entregaPort;
	private final FinanceiroPort financeiroPort;

	public GeradorNotaFiscalServiceImpl(
			CalculadoraAliquotaProduto calculadoraAliquotaProduto,
			CalculadoraFrete calculadoraFrete,
			NotaFiscalFactory notaFiscalFactory,
			EstoquePort estoquePort,
			RegistroPort registroPort,
			EntregaPort entregaPort,
			FinanceiroPort financeiroPort) {
		this.calculadoraAliquotaProduto = calculadoraAliquotaProduto;
		this.calculadoraFrete = calculadoraFrete;
		this.notaFiscalFactory = notaFiscalFactory;
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

		double valorFreteComPercentual = calculadoraFrete.calcular(destinatario, pedido.getValorFrete());

		NotaFiscal notaFiscal = notaFiscalFactory.criar(pedido, itemNotaFiscalList, valorFreteComPercentual);

		estoquePort.enviarNotaFiscalParaBaixaEstoque(notaFiscal);
		registroPort.registrarNotaFiscal(notaFiscal);
		entregaPort.agendarEntrega(notaFiscal);
		financeiroPort.enviarNotaFiscalParaContasReceber(notaFiscal);

		return notaFiscal;
	}
}