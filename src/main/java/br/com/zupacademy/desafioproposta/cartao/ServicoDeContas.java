package br.com.zupacademy.desafioproposta.cartao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "contas", url = "${contas.url}")
public interface ServicoDeContas {

    @PostMapping
    String solicitaNovoCartao(NovoCartaoRequest cartaoRequest);

    @GetMapping
    Map<String, Object> consultaCartaoGerado(@RequestParam Integer idProposta);
}
