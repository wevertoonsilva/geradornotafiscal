package br.com.itau.geradornotafiscal.port.out;

import br.com.itau.geradornotafiscal.model.NotaFiscal;

public interface RegistroPort {
    void registrarNotaFiscal(NotaFiscal notaFiscal);
}
