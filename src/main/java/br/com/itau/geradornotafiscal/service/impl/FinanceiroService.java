package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.FinanceiroPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class FinanceiroService implements FinanceiroPort {

    private final MeterRegistry meterRegistry;

    public FinanceiroService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void enviarNotaFiscalParaContasReceber(NotaFiscal notaFiscal) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula o envio da nota fiscal para o contas a receber
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.financeiro.tempo"));
        }
    }
}
