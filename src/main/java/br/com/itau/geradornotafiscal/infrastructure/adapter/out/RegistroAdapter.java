package br.com.itau.geradornotafiscal.infrastructure.adapter.out;

import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;
import br.com.itau.geradornotafiscal.application.port.out.RegistroPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistroAdapter implements RegistroPort {

    private static final Logger log = LoggerFactory.getLogger(RegistroAdapter.class);

    private final MeterRegistry meterRegistry;

    public RegistroAdapter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void registrarNotaFiscal(NotaFiscal notaFiscal) {
        log.info("Iniciando registro da nota fiscal — idNota={}", notaFiscal.getIdNotaFiscal());
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula o registro da nota fiscal
            Thread.sleep(500);
            log.info("Conclusão do registro da nota fiscal — idNota={}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            log.error("Erro ao registrar nota fiscal — idNota={}", notaFiscal.getIdNotaFiscal(), e);
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.registro.tempo"));
        }
    }
}
