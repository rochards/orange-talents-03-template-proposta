package br.com.zupacademy.desafioproposta.cartao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "contas", url = "${contas.url}")
public interface ServicoDeContas {

    @PostMapping
    String solicitaNovoCartao(NovoCartaoRequest cartaoRequest);
}
