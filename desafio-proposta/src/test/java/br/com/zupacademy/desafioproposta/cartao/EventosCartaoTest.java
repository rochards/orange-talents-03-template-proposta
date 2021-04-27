package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.contas.ServicoDeContas;
import br.com.zupacademy.desafioproposta.proposta.Proposta;
import br.com.zupacademy.desafioproposta.proposta.PropostaRepository;
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

@ExtendWith(MockitoExtension.class)
class EventosCartaoTest {

    @InjectMocks
    private EventosCartao eventosCartao;
    @Mock
    private Transacao transacao;
    @Mock
    private ServicoDeContas servicoDeContas;
    @Mock
    private PropostaRepository propostaRepository;

    @Test
    @DisplayName("deveria buscar os bloqueios que ainda não tiveram o status mudado para BLOQUEADO no legado Serviço " +
            "de Contas")
    void bloqueiaCartoesTeste01() {
        var proposta = new Proposta("22339358027", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        proposta.atualizaStatus(true);
        ReflectionTestUtils.setField(proposta, "id", 1);

        var novoCartao = new Cartao("1234-1568-1587", LocalDateTime.now(), proposta);
        ReflectionTestUtils.setField(novoCartao, "id", 1);
        novoCartao.setStatus(BLOQUEADO);

        var bloqueio = new Bloqueio("127.0.0.1", "MockMvc", novoCartao);
        ReflectionTestUtils.setField(novoCartao, "id", 1);

        when(transacao.busca(Bloqueio.class, "statusCartaoNoLegado", ATIVO,100))
                .thenReturn(List.of(bloqueio));
        when(servicoDeContas.bloqueiaCartao(bloqueio.getCartao().getContasIdCartao(), Map.of("sistemaResponsavel",
                "API propostas")))
                .thenReturn(Map.of("resultado", "BLOQUEADO"));

        eventosCartao.bloqueiaCartoes();

        assertEquals(BLOQUEADO, bloqueio.getStatusCartaoNoLegado());
        verify(servicoDeContas, atLeastOnce()).bloqueiaCartao(bloqueio.getCartao().getContasIdCartao(),
                Map.of("sistemaResponsavel", "API propostas"));
        verify(transacao).atualizaEComita(bloqueio);
    }
}