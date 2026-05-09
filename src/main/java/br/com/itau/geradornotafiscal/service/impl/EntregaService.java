package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.EntregaPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EntregaService implements EntregaPort {

    private static final Logger log = LoggerFactory.getLogger(EntregaService.class);

    private final EntregaIntegrationPort entregaIntegrationPort;
    private final MeterRegistry meterRegistry;

    public EntregaService(EntregaIntegrationPort entregaIntegrationPort, MeterRegistry meterRegistry) {
        this.entregaIntegrationPort = entregaIntegrationPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void agendarEntrega(NotaFiscal notaFiscal) {
        log.info("Iniciando agendamento de entrega — idNota={}", notaFiscal.getIdNotaFiscal());
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula o agendamento da entrega
            Thread.sleep(150);
            entregaIntegrationPort.criarAgendamentoEntrega(notaFiscal);
            log.info("Conclusão do agendamento de entrega — idNota={}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            log.error("Erro ao agendar entrega — idNota={}", notaFiscal.getIdNotaFiscal(), e);
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.entrega.tempo"));
        }
    }
}
