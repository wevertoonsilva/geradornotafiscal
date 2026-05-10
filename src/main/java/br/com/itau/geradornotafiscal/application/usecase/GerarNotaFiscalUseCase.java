package br.com.itau.geradornotafiscal.application.usecase;

import br.com.itau.geradornotafiscal.application.port.in.GerarNotaFiscalPort;
import br.com.itau.geradornotafiscal.domain.model.*;
import br.com.itau.geradornotafiscal.application.port.out.EntregaPort;
import br.com.itau.geradornotafiscal.application.port.out.EstoquePort;
import br.com.itau.geradornotafiscal.application.port.out.FinanceiroPort;
import br.com.itau.geradornotafiscal.application.port.out.RegistroPort;
import br.com.itau.geradornotafiscal.domain.policy.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;

@Service
public class GerarNotaFiscalUseCase implements GerarNotaFiscalPort{

	private static final Logger log = LoggerFactory.getLogger(GerarNotaFiscalUseCase.class);

	private final CalculadoraAliquotaProduto calculadoraAliquotaProduto;
	private final CalculadoraFrete calculadoraFrete;
	private final NotaFiscalFactory notaFiscalFactory;

	private final EstoquePort estoquePort;
	private final RegistroPort registroPort;
	private final EntregaPort entregaPort;
	private final FinanceiroPort financeiroPort;
	private final MeterRegistry meterRegistry;

	public GerarNotaFiscalUseCase(
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
		log.info("Gerando nota fiscal — idPedido={} tipoPessoa={} itens={}",
				pedido.getIdPedido(), pedido.getDestinatario().getTipoPessoa(), pedido.getItens().size());

		Timer.Sample sample = Timer.start(meterRegistry);
		try {
			NotaFiscal notaFiscal = processarGeracao(pedido);

			log.info("Nota fiscal gerada — idPedido={} idNotaFiscal={}",
					pedido.getIdPedido(), notaFiscal.getIdNotaFiscal());

			return notaFiscal;
		} finally {
			long tempoTotal = sample.stop(meterRegistry.timer("notas.fiscais.geracao.tempo"));
			log.info("Conclusão do processamento — idPedido={} tempoTotalMs={}",
					pedido.getIdPedido(), tempoTotal / 1_000_000);
		}
	}

	private NotaFiscal processarGeracao(Pedido pedido) {

		Destinatario destinatario = pedido.getDestinatario();
		TipoPessoa tipoPessoa = destinatario.getTipoPessoa();
		RegimeTributacaoPJ regimeTributacao = destinatario.getRegimeTributacao();

		AliquotaStrategy strategy = AliquotaStrategyFactory.getStrategy(tipoPessoa, regimeTributacao);
		BigDecimal aliquota = strategy.calcularAliquota(pedido.getValorTotalItens());
		log.info("Alíquota determinada — idPedido={} aliquota={}", pedido.getIdPedido(), aliquota);

		List<ItemNotaFiscal> itemNotaFiscalList = calculadoraAliquotaProduto.calcularAliquota(pedido.getItens(), aliquota);

		BigDecimal valorFreteComPercentual = calculadoraFrete.calcular(destinatario, pedido.getValorFrete());
		log.info("Frete calculado — idPedido={} valorFrete={}", pedido.getIdPedido(), valorFreteComPercentual);

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
