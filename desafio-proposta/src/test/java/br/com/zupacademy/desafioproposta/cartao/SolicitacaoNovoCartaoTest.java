package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.SolicitacaoNovoCartao;
import br.com.zupacademy.desafioproposta.contas.NovoCartaoRequest;
import br.com.zupacademy.desafioproposta.contas.ServicoDeContas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolicitacaoNovoCartaoTest {

    @InjectMocks
    private SolicitacaoNovoCartao solicitacaoNovoCartao;
    @Mock
    private ServicoDeContas servicoDeContas;

    @Test
    @DisplayName("deveria solicitar um novo cartão se o serviço de contas estiver UP")
    void solicitaTeste01() {
        var novoCartaoRequest = new NovoCartaoRequest("22339358027", "Peter Parker", 1);

        solicitacaoNovoCartao.solicita(novoCartaoRequest);

        verify(servicoDeContas).solicitaNovoCartao(novoCartaoRequest);
    }
}