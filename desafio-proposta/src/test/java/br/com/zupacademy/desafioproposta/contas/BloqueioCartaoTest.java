package br.com.zupacademy.desafioproposta.contas;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.bloqueio.Bloqueio;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static br.com.zupacademy.desafioproposta.cartao.StatusCartao.ATIVO;
import static br.com.zupacademy.desafioproposta.cartao.StatusCartao.BLOQUEADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BloqueioCartaoTest {

    @InjectMocks
    private BloqueioCartao bloqueioCartao;
    @Mock
    private Transacao transacao;
    @Mock
    private ServicoDeContas servicoDeContas;

    private Proposta proposta;
    private Cartao cartao;
    private Bloqueio bloqueio;

    @BeforeEach
    void setUp() {
        proposta = new Proposta("22339358027", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        proposta.atualizaStatus(true);
        ReflectionTestUtils.setField(proposta, "id", 1);

        cartao = new Cartao("1234-1568-1587", LocalDateTime.now(), proposta);
        ReflectionTestUtils.setField(cartao, "id", 1);
        cartao.setStatus(BLOQUEADO);

        bloqueio = new Bloqueio("127.0.0.1", "MockMvc", cartao);
        ReflectionTestUtils.setField(cartao, "id", 1);
    }

    @Test
    @DisplayName("deveria buscar os bloqueios que ainda não tiveram o status mudado para BLOQUEADO no legado Serviço " +
            "de Contas")
    void bloqueiaTeste01() {
        when(transacao.busca(Bloqueio.class, "statusCartaoNoLegado", ATIVO,100))
                .thenReturn(List.of(bloqueio));
        when(servicoDeContas.bloqueiaCartao(bloqueio.getContasIdCartao(), Map.of("sistemaResponsavel",
                "API propostas")))
                .thenReturn(Map.of("resultado", "BLOQUEADO"));

        bloqueioCartao.bloqueia();

        assertEquals(BLOQUEADO, bloqueio.getStatusCartaoNoLegado());
        verify(servicoDeContas).bloqueiaCartao(bloqueio.getContasIdCartao(), Map.of("sistemaResponsavel", "API propostas"));
        verify(transacao).atualizaEComita(bloqueio);
    }

    @Test
    @DisplayName("não deveria chamar o serviço de contas quando não houver bloqueios com status BLOQUEADO")
    void bloqueiaTeste02() {
        when(transacao.busca(Bloqueio.class, "statusCartaoNoLegado", ATIVO,100))
                .thenReturn(List.of());

        bloqueioCartao.bloqueia();

        verify(servicoDeContas, never()).bloqueiaCartao(anyString(), any(Map.class));
    }

    @Test
    @DisplayName("deveria lançar uma exceção por problemas no serviço de contas")
    void bloqueiaTeste03() {
        when(transacao.busca(Bloqueio.class, "statusCartaoNoLegado", ATIVO,100))
                .thenReturn(List.of(bloqueio));
        when(servicoDeContas.bloqueiaCartao(bloqueio.getContasIdCartao(), Map.of("sistemaResponsavel",
                "API propostas")))
                .thenThrow(FeignException.class);

        bloqueioCartao.bloqueia();

        verify(servicoDeContas).bloqueiaCartao(bloqueio.getContasIdCartao(), Map.of("sistemaResponsavel",
                "API propostas"));
        verify(transacao, never()).atualizaEComita(bloqueio);
    }
}