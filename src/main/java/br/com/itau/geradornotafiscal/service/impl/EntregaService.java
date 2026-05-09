package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.EntregaPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class EntregaService implements EntregaPort {

    private final EntregaIntegrationPort entregaIntegrationPort;
    private final MeterRegistry meterRegistry;

    public EntregaService(EntregaIntegrationPort entregaIntegrationPort, MeterRegistry meterRegistry) {
        this.entregaIntegrationPort = entregaIntegrationPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void agendarEntrega(NotaFiscal notaFiscal) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula o agendamento da entrega
            Thread.sleep(150);
            entregaIntegrationPort.criarAgendamentoEntrega(notaFiscal);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.entrega.tempo"));
        }
    }
}
