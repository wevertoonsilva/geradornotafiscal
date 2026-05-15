package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.DestinatarioRequest;
import br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest.generated.model.PedidoRequest;
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
            DestinatarioRequest.TipoPessoaEnum.PJ.equals(pedido.getDestinatario().getTipoPessoa()) &&
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

        // Se houver qualquer item com valor unitário nulo, não podemos reconciliar com segurança
        boolean possuiItemInvalido = pedido.getItens().stream()
                .anyMatch(item -> item.getValorUnitario() == null);

        if (possuiItemInvalido) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Existem itens com valor unitário não informado")
                    .addPropertyNode("itens")
                    .addConstraintViolation();
            return false;
        }

        BigDecimal somaItens = pedido.getItens().stream()
                .map(item -> item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return somaItens.compareTo(pedido.getValorTotalItens()) == 0;
    }
}
