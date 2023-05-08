package com.food.pagamentos.domain.service;

import com.food.pagamentos.api.dto.PagamentoDTO;
import com.food.pagamentos.api.http.PedidoClient;
import com.food.pagamentos.domain.enums.Status;
import com.food.pagamentos.domain.exception.PagamentoNaoExisteException;
import com.food.pagamentos.domain.model.Pagamento;
import com.food.pagamentos.domain.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    public static final String MSG_PAGAMENTO_NAO_EXISTE = "Não existe um pagamento com esse Id";
    private final PagamentoRepository pagamentoRepository;
    private final ModelMapper modelMapper;
    private final PedidoClient pedidoClient;

    public Page<PagamentoDTO> buscarTodos(Pageable pageable) {
        return pagamentoRepository.findAll(pageable)
                .map(p -> modelMapper.map(p, PagamentoDTO.class));
    }

    public PagamentoDTO buscarPorId(Long id) {
        Pagamento pagamento = buscarOuFalhar(id);
        PagamentoDTO pagamentoDTO = modelMapper.map(pagamento, PagamentoDTO.class);
        pagamentoDTO.setItens(pedidoClient.obterItensDoPedido(pagamento.getPedidoId()).getItens());
        return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    public PagamentoDTO criarPagamento(PagamentoDTO dto) {
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    public PagamentoDTO atualizarPagamento(Long id, PagamentoDTO pagamentoDTO) {
        buscarOuFalhar(id);
        Pagamento pagamento = modelMapper.map(pagamentoDTO, Pagamento.class);
        pagamento.setId(id);
        pagamento = pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    public void excluir(Long id) {
        buscarOuFalhar(id);
        pagamentoRepository.deleteById(id);
    }

    public void confirmarPagamento(Long id) {
        Pagamento pagamento = buscarOuFalhar(id);
        pagamento.setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento);
        pedidoClient.atulizarPagamento(id);

    }

    public Pagamento buscarOuFalhar(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new PagamentoNaoExisteException(MSG_PAGAMENTO_NAO_EXISTE));
    }

    public void alteraStatus(Long id) {
        Pagamento pagamento = buscarOuFalhar(id);
        pagamento.setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        pagamentoRepository.save(pagamento);
    }
}
