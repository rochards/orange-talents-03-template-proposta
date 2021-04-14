package br.com.zupacademy.desafioproposta.proposta.financeiro;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "solicitacaoPropostas", url = "${financeiro.url}")
public interface ConsultaFinanceiro {

    @PostMapping
    SolicitacaoResponse analisaSolicitacaoDeProposta(SolicitacaoRequest solicitacaoRequest);
}
