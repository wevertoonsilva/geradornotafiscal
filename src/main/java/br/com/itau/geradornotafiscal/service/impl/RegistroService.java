package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.RegistroPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class RegistroService implements RegistroPort {

    private final MeterRegistry meterRegistry;

    public RegistroService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void registrarNotaFiscal(NotaFiscal notaFiscal) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula o registro da nota fiscal
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.registro.tempo"));
        }
    }
}
