package com.food.pagamentos.controller;

import com.food.pagamentos.dto.PagamentoDTO;
import com.food.pagamentos.service.PagamentoService;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

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
        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamentoDTO.getId()).toUri();
        return pagamentoService.criarPagamento(pagamentoDTO);
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
}
