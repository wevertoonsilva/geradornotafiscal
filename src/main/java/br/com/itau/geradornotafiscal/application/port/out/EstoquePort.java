package br.com.itau.geradornotafiscal.application.port.out;

import br.com.itau.geradornotafiscal.domain.model.NotaFiscal;

public interface EstoquePort {
    void enviarNotaFiscalParaBaixaEstoque(NotaFiscal notaFiscal);
}
