package br.com.zupacademy.desafioproposta.cartao.biometria;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cartoes")
public class NovaBiometriaController {

    private final Transacao transacao;

    public NovaBiometriaController(Transacao transacao) {
        this.transacao = transacao;
    }

    @PostMapping("/{idCartao}")
    public ResponseEntity<?> cadastra(@PathVariable String idCartao, UriComponentsBuilder uriBuilder,
                                      @RequestBody NovaBiometriaRequest biometriaRequest) {

        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }

        var novaBiometria = biometriaRequest.toModel(cartao);
        transacao.salvaEComita(novaBiometria);

        URI location = uriBuilder.path("/cartoes/{id}/biometrias/{id}").build(cartao.getContasIdCartao(), novaBiometria.getId());
        return ResponseEntity.created(location).build();
    }
}
