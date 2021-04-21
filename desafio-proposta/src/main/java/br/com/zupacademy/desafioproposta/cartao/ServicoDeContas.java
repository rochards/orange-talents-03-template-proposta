package br.com.zupacademy.desafioproposta.cartao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "contas", url = "${contas.url}")
public interface ServicoDeContas {

    @PostMapping
    CartaoResponse solicitaNovoCartao(NovoCartaoRequest cartaoRequest);

    @GetMapping
    CartaoResponse consultaCartaoGerado(@RequestParam Integer idProposta);
}
