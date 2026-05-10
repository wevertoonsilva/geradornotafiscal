package br.com.itau.geradornotafiscal.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Item {
    private String idItem;
    private String descricao;
    private BigDecimal valorUnitario;
    private int quantidade;
}

