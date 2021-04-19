package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.financeiro.ConsultaFinanceiro;
import br.com.zupacademy.desafioproposta.financeiro.SolicitacaoRequest;
import br.com.zupacademy.desafioproposta.financeiro.SolicitacaoResponse;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static br.com.zupacademy.desafioproposta.financeiro.StatusSolicitacao.SEM_RESTRICAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AnalisaNovaPropostaTest {

    @InjectMocks
    private AnalisaNovaProposta analisaNovaProposta;
    @Mock
    private ConsultaFinanceiro consultaFinanceiro;

    private SolicitacaoRequest solicitacaoRequest;

    @BeforeEach
    void setUp() {
        this.solicitacaoRequest = new SolicitacaoRequest("22339358027", "Peter Parker", "1");
    }

    @Test
    @DisplayName("proposta deveria ser elegível para retorno do financeiro SEM_RESTRICAO")
    void semRestricaoTeste01() {
        var solicitacaoResponse = new SolicitacaoResponse("22339358027", "Peter Parker", SEM_RESTRICAO, "1");

        when(consultaFinanceiro.analisaSolicitacaoDeProposta(solicitacaoRequest))
                .thenReturn(solicitacaoResponse);

        boolean ehElegivel = analisaNovaProposta.semRestricao(solicitacaoRequest.getDocumento(),
                solicitacaoRequest.getNome(), solicitacaoRequest.getIdProposta());

        assertThat(ehElegivel).isTrue();
    }

    @Test
    @DisplayName("proposta não deveria ser elegível caso o cliente feign jogar a exceção UnprocessableEntity")
    void semRestricaoTeste02() {
        doThrow(FeignException.UnprocessableEntity.class)
                .when(consultaFinanceiro).analisaSolicitacaoDeProposta(solicitacaoRequest);

        boolean ehElegivel = analisaNovaProposta.semRestricao(solicitacaoRequest.getDocumento(),
                solicitacaoRequest.getNome(), solicitacaoRequest.getIdProposta());

        assertThat(ehElegivel).isFalse();
    }

    @Test
    @DisplayName("proposta não deveria ser elegível caso o cliente feign jogar a exceção FeignException")
    void semRestricaoTeste03() {
        doThrow(FeignException.class)
                .when(consultaFinanceiro).analisaSolicitacaoDeProposta(solicitacaoRequest);

        boolean ehElegivel = analisaNovaProposta.semRestricao(solicitacaoRequest.getDocumento(),
                solicitacaoRequest.getNome(), solicitacaoRequest.getIdProposta());

        assertThat(ehElegivel).isFalse();
    }
}
