package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.EntregaPort;
import org.springframework.stereotype.Service;

@Service
public class EntregaService implements EntregaPort {

    private final EntregaIntegrationPort entregaIntegrationPort;

    public EntregaService(EntregaIntegrationPort entregaIntegrationPort) {
        this.entregaIntegrationPort = entregaIntegrationPort;
    }

    @Override
    public void agendarEntrega(NotaFiscal notaFiscal) {

            try {
                //Simula o agendamento da entrega
                Thread.sleep(150);
                entregaIntegrationPort.criarAgendamentoEntrega(notaFiscal);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

    }
}
