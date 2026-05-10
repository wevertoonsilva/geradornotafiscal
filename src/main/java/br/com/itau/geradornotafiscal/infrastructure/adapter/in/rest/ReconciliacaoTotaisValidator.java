package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ReconciliacaoTotaisValidator implements ConstraintValidator<ReconciliacaoTotais, PedidoRequest> {

    @Override
    public boolean isValid(PedidoRequest pedido, ConstraintValidatorContext context) {
        if (pedido == null) {
            return true;
        }

        // Validação de PJ com Regime Tributação null
        if (pedido.getDestinatario() != null && 
            br.com.itau.geradornotafiscal.domain.model.TipoPessoa.JURIDICA.equals(pedido.getDestinatario().getTipoPessoa()) && 
            pedido.getDestinatario().getRegimeTributacao() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("PJ deve ter regime de tributação informado")
                    .addPropertyNode("destinatario.regimeTributacao")
                    .addConstraintViolation();
            return false;
        }

        if (pedido.getItens() == null || pedido.getValorTotalItens() == null) {
            return true;
        }

        BigDecimal somaItens = pedido.getItens().stream()
                .filter(item -> item.getValorUnitario() != null)
                .map(item -> item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return somaItens.compareTo(pedido.getValorTotalItens()) == 0;
    }
}
