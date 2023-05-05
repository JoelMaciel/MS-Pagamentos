package com.food.pagamentos.service;

import com.food.pagamentos.dto.PagamentoDTO;
import com.food.pagamentos.exception.PagamentoNaoExisteException;
import com.food.pagamentos.model.Pagamento;
import com.food.pagamentos.repository.PagamentoRepository;
import com.food.pagamentos.status.Status;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    public static final String MSG_PAGAMENTO_NAO_EXISTE = "Não existe um pagamento com esse Id";
    private final PagamentoRepository pagamentoRepository;
    private final ModelMapper modelMapper;

    public Page<PagamentoDTO> buscarTodos(Pageable pageable) {
        return pagamentoRepository.findAll(pageable)
                .map(p -> modelMapper.map(p, PagamentoDTO.class));
    }

    public PagamentoDTO buscarPorId(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new PagamentoNaoExisteException(MSG_PAGAMENTO_NAO_EXISTE));
        return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    public PagamentoDTO criarPagamento(PagamentoDTO pagamentoDTO) {
        Pagamento pagamento = modelMapper.map(pagamentoDTO, Pagamento.class);
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

    public Pagamento buscarOuFalhar(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new PagamentoNaoExisteException(MSG_PAGAMENTO_NAO_EXISTE));
    }

}
