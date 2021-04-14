package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.proposta.financeiro.ConsultaFinanceiro;
import br.com.zupacademy.desafioproposta.proposta.financeiro.SolicitacaoRequest;
import br.com.zupacademy.desafioproposta.proposta.financeiro.StatusSolicitacao;
import org.springframework.stereotype.Component;

@Component
public class AnalisaNovaProposta {

    private final ConsultaFinanceiro consultaFinanceiro;

    public AnalisaNovaProposta(ConsultaFinanceiro consultaFinanceiro) {
        this.consultaFinanceiro = consultaFinanceiro;
    }

    public boolean semRestricao(String documentoSolicitante, String nomeSolicitante, String idProposta) {
        var solicitacaoRequest = new SolicitacaoRequest(documentoSolicitante, nomeSolicitante, idProposta);
        var solicitacaoResponse = consultaFinanceiro.analisaSolicitacaoDeProposta(solicitacaoRequest);

        return solicitacaoResponse.getResultadoSolicitacao() == StatusSolicitacao.SEM_RESTRICAO;
    }
}
