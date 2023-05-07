package com.food.pagamentos.api.dto;

import com.food.pagamentos.domain.enums.Status;
import com.food.pagamentos.domain.model.ItemDoPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PagamentoDTO {

    private Long id;
    private BigDecimal valor;
    private String nome;
    private String numero;
    private String expiracao;
    private String codigo;
    private Status status;
    private Long formaDePagamentoId;
    private Long pedidoId;
    private List<ItemDoPedido> itens;

}
