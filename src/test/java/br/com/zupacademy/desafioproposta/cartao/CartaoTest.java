package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.proposta.Proposta;
import br.com.zupacademy.desafioproposta.proposta.PropostaRepository;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static br.com.zupacademy.desafioproposta.proposta.StatusProposta.ELEGIVEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartaoTest {

    @InjectMocks
    private Cartao cartao;
    @Mock
    private Transacao transacao;
    @Mock
    private ServicoDeContas servicoDeContas;
    @Mock
    private PropostaRepository propostaRepository;

    @Test
    @DisplayName("deveria solicitar um novo cartão se o serviço de contas estiver UP")
    void solicitaNovoTeste01() {
        var novoCartaoRequest = new NovoCartaoRequest("22339358027", "Peter Parker", 1);

        when(servicoDeContas.solicitaNovoCartao(novoCartaoRequest))
                .thenReturn(""); // o teste nao funciona como anyString(). O retorno desse metodo nao me importa

        cartao.solicitaNovo("22339358027", "Peter Parker", 1);

        verify(servicoDeContas, atLeastOnce()).solicitaNovoCartao(novoCartaoRequest);
    }

    @Test
    @DisplayName("se não há propostas elegíveis sem cartão, não deveria chamar o serviço de contas")
    void buscaGeradosTeste01() {
        when(propostaRepository.findFirst100ByStatusAndIdCartaoNull(ELEGIVEL))
                .thenReturn(List.of());

        cartao.buscaGerados();

        verify(servicoDeContas, never()).consultaCartaoGerado(anyInt());
    }

    @Test
    @DisplayName("deveria buscar os cartões para a lista de propostas elegíveis que estão sem cartões e associar cada" +
            " cartão à sua proposta no banco")
    void buscaGeradosTeste02() {
        var proposta = new Proposta("22339358027", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        ReflectionTestUtils.setField(proposta, "id", 1); // em Proposta não tem um setId, por isso fiz por Reflection

        when(propostaRepository.findFirst100ByStatusAndIdCartaoNull(ELEGIVEL))
                .thenReturn(List.of(proposta));
        when(servicoDeContas.consultaCartaoGerado(1))
                .thenReturn(Map.of("id", "1234-1568-1587", "titular", "Peter Parker"));

        cartao.buscaGerados();

        verify(servicoDeContas, atLeastOnce()).consultaCartaoGerado(1);
        verify(transacao, atLeastOnce()).atualizaEComita(proposta);
        assertEquals("1234-1568-1587", proposta.getIdCartao());
    }

    @Test
    @DisplayName("deveria lançar uma exceção quando o cartão ainda não estiver pronto")
    void buscaGeradosTeste03() {
        var proposta = new Proposta("22339358027", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        ReflectionTestUtils.setField(proposta, "id", 1);

        when(propostaRepository.findFirst100ByStatusAndIdCartaoNull(ELEGIVEL))
                .thenReturn(List.of(proposta));
        doThrow(FeignException.InternalServerError.class)
                .when(servicoDeContas).consultaCartaoGerado(1);

        cartao.buscaGerados();

        verify(servicoDeContas, atLeastOnce()).consultaCartaoGerado(1);
        verify(transacao, never()).atualizaEComita(proposta);
        assertNull(proposta.getIdCartao());
    }

    @Test
    @DisplayName("deveria lançar uma exceção quando o serviço de contas estiver DOWN")
    void buscaGeradosTeste04() {
        var proposta = new Proposta("22339358027", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        ReflectionTestUtils.setField(proposta, "id", 1);

        when(propostaRepository.findFirst100ByStatusAndIdCartaoNull(ELEGIVEL))
                .thenReturn(List.of(proposta));
        doThrow(FeignException.class)
                .when(servicoDeContas).consultaCartaoGerado(1);

        cartao.buscaGerados();

        verify(servicoDeContas, atLeastOnce()).consultaCartaoGerado(1);
        verify(transacao, never()).atualizaEComita(proposta);
        assertNull(proposta.getIdCartao());
    }

    @Test
    @DisplayName("não deveria atualizar a proposta se o nome do solicitante não é igual ao titular do cartão " +
            "retornado pelo sistema de contas")
    void buscaGeradosTeste05() {
        var proposta = new Proposta("22339358027", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        ReflectionTestUtils.setField(proposta, "id", 1); // em Proposta não tem um setId, por isso fiz por Reflection

        when(propostaRepository.findFirst100ByStatusAndIdCartaoNull(ELEGIVEL))
                .thenReturn(List.of(proposta));
        when(servicoDeContas.consultaCartaoGerado(1))
                .thenReturn(Map.of("id", "1234-1568-1587", "titular", "Homem Aranha"));

        cartao.buscaGerados();

        verify(servicoDeContas, atLeastOnce()).consultaCartaoGerado(1);
        verify(transacao, never()).atualizaEComita(proposta);
        assertNull(proposta.getIdCartao());
    }
}