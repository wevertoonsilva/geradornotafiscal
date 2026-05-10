package br.com.itau.geradornotafiscal.infrastructure.adapter.out;

import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;
import br.com.itau.geradornotafiscal.application.port.out.EntregaPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EntregaAdapter implements EntregaPort {

    private static final Logger log = LoggerFactory.getLogger(EntregaAdapter.class);

    private final EntregaExternalClient entregaExternalClient;
    private final MeterRegistry meterRegistry;

    public EntregaAdapter(EntregaExternalClient entregaExternalClient, MeterRegistry meterRegistry) {
        this.entregaExternalClient = entregaExternalClient;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void agendarEntrega(NotaFiscal notaFiscal) {
        log.info("Iniciando agendamento de entrega — idNota={}", notaFiscal.getIdNotaFiscal());
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula o agendamento da entrega
            Thread.sleep(150);
            entregaExternalClient.criarAgendamentoEntrega(notaFiscal);
            log.info("Conclusão do agendamento de entrega — idNota={}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            log.error("Erro ao agendar entrega — idNota={}", notaFiscal.getIdNotaFiscal(), e);
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.entrega.tempo"));
        }
    }
}
