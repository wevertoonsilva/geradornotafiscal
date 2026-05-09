package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.FinanceiroPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FinanceiroService implements FinanceiroPort {

    private static final Logger log = LoggerFactory.getLogger(FinanceiroService.class);

    private final MeterRegistry meterRegistry;

    public FinanceiroService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void enviarNotaFiscalParaContasReceber(NotaFiscal notaFiscal) {
        log.info("Iniciando envio para o financeiro — idNota={}", notaFiscal.getIdNotaFiscal());
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            //Simula o envio da nota fiscal para o contas a receber
            Thread.sleep(250);
            log.info("Conclusão do envio para o financeiro — idNota={}", notaFiscal.getIdNotaFiscal());
        } catch (InterruptedException e) {
            log.error("Erro ao enviar para o financeiro — idNota={}", notaFiscal.getIdNotaFiscal(), e);
            throw new RuntimeException(e);
        } finally {
            sample.stop(meterRegistry.timer("integracao.financeiro.tempo"));
        }
    }
}
