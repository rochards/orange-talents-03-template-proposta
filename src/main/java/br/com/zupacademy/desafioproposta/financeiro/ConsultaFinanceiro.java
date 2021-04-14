package br.com.zupacademy.desafioproposta.financeiro;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "solicitacaoPropostas", url = "${financeiro.url}")
public interface ConsultaFinanceiro {

    @PostMapping
    SolicitacaoResponse analisaSolicitacaoDeProposta(SolicitacaoRequest solicitacaoRequest);

    @RequestMapping(method = RequestMethod.OPTIONS)
    void isUp();
}
