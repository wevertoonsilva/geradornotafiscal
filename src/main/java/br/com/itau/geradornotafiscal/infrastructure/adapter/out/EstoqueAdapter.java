package br.com.itau.geradornotafiscal.infrastructure.adapter.out;

import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;
import br.com.itau.geradornotafiscal.application.port.out.EstoquePort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EstoqueAdapter implements EstoquePort {

    private static final Logger log = LoggerFactory.getLogger(EstoqueAdapter.class);

    private final MeterRegistry meterRegistry;

    public EstoqueAdapter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void enviarNotaFiscalParaBaixaEstoque(NotaFiscal notaFiscal) {
        log.info("Iniciando baixa de estoque — idNota={}", notaFiscal.getIdNotaFiscal());
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula envio de nota fiscal para baixa de estoque
            Thread.sleep(380);
            log.info("Conclusão da baixa de estoque — idNota={}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            log.error("Erro ao baixar estoque — idNota={}", notaFiscal.getIdNotaFiscal(), e);
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.estoque.tempo"));
        }
    }
}
