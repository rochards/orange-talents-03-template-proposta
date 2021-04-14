package br.com.zupacademy.desafioproposta.proposta.financeiro;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "solicitacaoPropostas", url = "http://localhost:9999")
public interface ConsultaFinanceiro {

    @PostMapping(value = "/api/solicitacao")
    SolicitacaoResponse analisaSolicitacaoDeProposta(SolicitacaoRequest solicitacaoRequest);
}
