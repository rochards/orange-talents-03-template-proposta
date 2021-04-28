package br.com.zupacademy.desafioproposta.contas;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.avisoviagem.AvisoViagem;
import br.com.zupacademy.desafioproposta.cartao.avisoviagem.AvisoViagemRequest;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.proposta.Proposta;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.zupacademy.desafioproposta.cartao.avisoviagem.StatusAvisoViagem.CRIADO;
import static br.com.zupacademy.desafioproposta.cartao.avisoviagem.StatusAvisoViagem.NAO_CRIADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoViagemTest {

    @InjectMocks
    private NotificacaoViagem notificacaoViagem;
    @Mock
    private Transacao transacao;
    @Mock
    private ServicoDeContas servicoDeContas;

    private Proposta proposta;
    private Cartao cartao;
    private AvisoViagem avisoViagem;

    @BeforeEach
    void setUp() {
        proposta = new Proposta("22339358027", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        proposta.atualizaStatus(true);
        ReflectionTestUtils.setField(proposta, "id", 1);

        cartao = new Cartao("1234-1568-1587", LocalDateTime.now(), proposta);
        ReflectionTestUtils.setField(cartao, "id", 1);

        avisoViagem = new AvisoViagem("Disney", LocalDate.of(9999, 12, 31), "127.0.0.1", "MockMvc", cartao);
    }

    @Test
    @DisplayName("deveria enviar notificação para o sistema legado para avisar sobre avisos de viagens")
    void notificaTeste01() {
        when(transacao.busca(AvisoViagem.class, "statusAvisoNoLegado", NAO_CRIADO, 100))
                .thenReturn(List.of(avisoViagem));

        notificacaoViagem.notifica();

        assertEquals(CRIADO, avisoViagem.getStatusAvisoNoLegado());
        verify(transacao).atualizaEComita(avisoViagem);
    }

    @Test
    @DisplayName("não deveria chamar o serviço de contas quando não houver avisos para enviar ao sistema legado")
    void notificaTeste02() {
        when(transacao.busca(AvisoViagem.class, "statusAvisoNoLegado", NAO_CRIADO, 100))
                .thenReturn(List.of());

        notificacaoViagem.notifica();

        verify(servicoDeContas, never()).notificaViagem(eq(avisoViagem.getContasIdCartao()),
                any(AvisoViagemRequest.class));
    }

    @Test
    @DisplayName("não deveria atualizar o aviso viagem quando o serviço de contas lançar exceção")
    void notificaTeste03() {
        when(transacao.busca(AvisoViagem.class, "statusAvisoNoLegado", NAO_CRIADO, 100))
                .thenReturn(List.of(avisoViagem));
        when(servicoDeContas.notificaViagem(eq(cartao.getContasIdCartao()), any(AvisoViagemRequest.class)))
                .thenThrow(FeignException.class);

        notificacaoViagem.notifica();

        assertEquals(NAO_CRIADO, avisoViagem.getStatusAvisoNoLegado());
        verify(transacao, never()).atualizaEComita(avisoViagem);
    }
}