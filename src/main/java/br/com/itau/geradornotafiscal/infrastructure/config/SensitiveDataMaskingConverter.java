package br.com.itau.geradornotafiscal.infrastructure.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Map;

public class SensitiveDataMaskingConverter extends ClassicConverter {

    private static final Map<String, String> PATTERNS = Map.of(
        "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",        "***.***.***-**",   // CPF
        "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", "**.***.***/**-**"  // CNPJ
    );

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        for (Map.Entry<String, String> entry : PATTERNS.entrySet()) {
            message = message.replaceAll(entry.getKey(), entry.getValue());
        }
        return message;
    }
}