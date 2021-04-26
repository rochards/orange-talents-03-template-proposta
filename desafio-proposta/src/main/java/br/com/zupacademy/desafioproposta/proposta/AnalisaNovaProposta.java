package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.financeiro.ServicoFinanceiro;
import br.com.zupacademy.desafioproposta.financeiro.SolicitacaoRequest;
import br.com.zupacademy.desafioproposta.financeiro.SolicitacaoResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static br.com.zupacademy.desafioproposta.financeiro.StatusSolicitacao.COM_RESTRICAO;
import static br.com.zupacademy.desafioproposta.financeiro.StatusSolicitacao.SEM_RESTRICAO;

@Component
public class AnalisaNovaProposta {

    private final Logger logger = LoggerFactory.getLogger(AnalisaNovaProposta.class);
    private final ServicoFinanceiro servicoFinanceiro;

    public AnalisaNovaProposta(ServicoFinanceiro servicoFinanceiro) {
        this.servicoFinanceiro = servicoFinanceiro;
    }

    public boolean semRestricao(String documentoSolicitante, String nomeSolicitante, String idProposta) {
        var solicitacaoRequest = new SolicitacaoRequest(documentoSolicitante, nomeSolicitante, idProposta);
        SolicitacaoResponse solicitacaoResponse = new SolicitacaoResponse();

        try {
            solicitacaoResponse = servicoFinanceiro.analisaSolicitacaoDeProposta(solicitacaoRequest);
        } catch (FeignException.UnprocessableEntity ex) {
            solicitacaoResponse.setResultadoSolicitacao(COM_RESTRICAO);
        } catch (FeignException ex) {
            solicitacaoResponse.setResultadoSolicitacao(COM_RESTRICAO);
            logger.error(ex.getMessage());
        }

        return solicitacaoResponse.getResultadoSolicitacao() == SEM_RESTRICAO;
    }
}
