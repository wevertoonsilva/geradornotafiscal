package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.port.out.EntregaPort;
import br.com.itau.geradornotafiscal.port.out.EstoquePort;
import br.com.itau.geradornotafiscal.port.out.FinanceiroPort;
import br.com.itau.geradornotafiscal.port.out.RegistroPort;
import br.com.itau.geradornotafiscal.service.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService{

	private final CalculadoraAliquotaProduto calculadoraAliquotaProduto;
	private final CalculadoraFrete calculadoraFrete;
	private final NotaFiscalFactory notaFiscalFactory;

	private final EstoquePort estoquePort;
	private final RegistroPort registroPort;
	private final EntregaPort entregaPort;
	private final FinanceiroPort financeiroPort;
	private final MeterRegistry meterRegistry;

	public GeradorNotaFiscalServiceImpl(
			CalculadoraAliquotaProduto calculadoraAliquotaProduto,
			CalculadoraFrete calculadoraFrete,
			NotaFiscalFactory notaFiscalFactory,
			EstoquePort estoquePort,
			RegistroPort registroPort,
			EntregaPort entregaPort,
			FinanceiroPort financeiroPort,
			MeterRegistry meterRegistry) {
		this.calculadoraAliquotaProduto = calculadoraAliquotaProduto;
		this.calculadoraFrete = calculadoraFrete;
		this.notaFiscalFactory = notaFiscalFactory;
		this.estoquePort = estoquePort;
		this.registroPort = registroPort;
		this.entregaPort = entregaPort;
		this.financeiroPort = financeiroPort;
		this.meterRegistry = meterRegistry;
	}

	@Override
	public NotaFiscal gerarNotaFiscal(Pedido pedido) {
		Timer.Sample sample = Timer.start(meterRegistry);
		try {
			return processarGeracao(pedido);
		} finally {
			sample.stop(meterRegistry.timer("notas.fiscais.geracao.tempo"));
		}
	}

	private NotaFiscal processarGeracao(Pedido pedido) {

		Destinatario destinatario = pedido.getDestinatario();
		TipoPessoa tipoPessoa = destinatario.getTipoPessoa();
		RegimeTributacaoPJ regimeTributacao = destinatario.getRegimeTributacao();

		AliquotaStrategy strategy = AliquotaStrategyFactory.getStrategy(tipoPessoa, regimeTributacao);
		BigDecimal aliquota = strategy.calcularAliquota(pedido.getValorTotalItens());

		List<ItemNotaFiscal> itemNotaFiscalList = calculadoraAliquotaProduto.calcularAliquota(pedido.getItens(), aliquota);

		BigDecimal valorFreteComPercentual = calculadoraFrete.calcular(destinatario, pedido.getValorFrete());

		NotaFiscal notaFiscal = notaFiscalFactory.criar(pedido, itemNotaFiscalList, valorFreteComPercentual);

		meterRegistry.counter("notas.fiscais.geradas", "tipo_pessoa", tipoPessoa.name()).increment();

		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			executor.submit(() -> {
				try {
					estoquePort.enviarNotaFiscalParaBaixaEstoque(notaFiscal);
				} catch (Exception e) {
					log.error("Falha ao baixar estoque - idNota={}", notaFiscal.getIdNotaFiscal(), e);
				}
			});
			executor.submit(() -> {
				try {
					registroPort.registrarNotaFiscal(notaFiscal);
				} catch (Exception e) {
					log.error("Falha ao registrar nota fiscal - idNota={}", notaFiscal.getIdNotaFiscal(), e);
				}
			});
			executor.submit(() -> {
				try {
					entregaPort.agendarEntrega(notaFiscal);
				} catch (Exception e) {
					log.error("Falha ao agendar entrega - idNota={}", notaFiscal.getIdNotaFiscal(), e);
				}
			});
			executor.submit(() -> {
				try {
					financeiroPort.enviarNotaFiscalParaContasReceber(notaFiscal);
				} catch (Exception e) {
					log.error("Falha ao enviar para o financeiro - idNota={}", notaFiscal.getIdNotaFiscal(), e);
				}
			});
		}

		return notaFiscal;
	}
}