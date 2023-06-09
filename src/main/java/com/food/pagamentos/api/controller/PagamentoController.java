package com.food.pagamentos.api.controller;

import com.food.pagamentos.api.dto.PagamentoDTO;
import com.food.pagamentos.domain.service.PagamentoService;
import com.sun.istack.NotNull;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private  PagamentoService pagamentoService;
    @Autowired
    private  RabbitTemplate rabbitTemplate;

    @GetMapping
    public Page<PagamentoDTO> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return pagamentoService.buscarTodos(pageable);
    }

    @GetMapping("/{id}")
    public PagamentoDTO buscarPorId(@PathVariable @NotNull Long id) {
        return pagamentoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoDTO cadastrar(@RequestBody @Valid PagamentoDTO pagamentoDTO, UriComponentsBuilder uriBuilder) {
        PagamentoDTO pagamento = pagamentoService.criarPagamento(pagamentoDTO);
        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        rabbitTemplate.convertAndSend("pagamento.ex", "", pagamento);
        return pagamento;
    }

    @PutMapping("/{id}")
    public PagamentoDTO atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDTO pagamentoDTO) {
        return pagamentoService.atualizarPagamento(id, pagamentoDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable @NotNull Long id) {
        pagamentoService.excluir(id);
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    public void confirmarPagamento(@PathVariable @NotNull Long id) {
        pagamentoService.confirmarPagamento(id);
    }
    public void pagamentoAutorizadoComIntegracaoPendente(Long id , Exception e) {
        pagamentoService.alteraStatus(id);
    }
}
