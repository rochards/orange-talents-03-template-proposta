package br.com.zupacademy.desafioproposta.contas;

import br.com.zupacademy.desafioproposta.cartao.avisoviagem.AvisoViagemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "contas", url = "${contas.url}")
public interface ServicoDeContas {

    @PostMapping
    CartaoResponse solicitaNovoCartao(NovoCartaoRequest cartaoRequest);

    @GetMapping
    CartaoResponse consultaCartaoGerado(@RequestParam Integer idProposta);

    @PostMapping("/{contasIdCartao}/bloqueios")
    Map<String, String> bloqueiaCartao(@PathVariable String contasIdCartao, Map<String, String> sistemaResponsavel);

    @PostMapping("/{contasIdCartao}/avisos")
    Map<String, String> notificaViagem(@PathVariable String contasIdCartao, AvisoViagemRequest viagemRequest);

    @PostMapping("/{contasIdCartao}/carteiras")
    CarteiraResponse associaCartaoACarteira(@PathVariable String contasIdCartao, NovaCarteiraRequest novaCarteiraRequest);
}
