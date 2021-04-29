package br.com.zupacademy.desafioproposta.cartao.biometria;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import io.opentracing.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/cartoes")
public class NovaBiometriaController {

    private final Tracer tracer;
    private final Transacao transacao;

    public NovaBiometriaController(Tracer tracer, Transacao transacao) {
        this.tracer = tracer;
        this.transacao = transacao;
    }

    @PostMapping("/{idCartao}")
    public ResponseEntity<?> cadastra(@PathVariable String idCartao, UriComponentsBuilder uriBuilder,
                                      @RequestBody @Valid NovaBiometriaRequest biometriaRequest, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new APIErrorHandler(result.getFieldErrors()));
        }

        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }

        var activeSpan = tracer.activeSpan();
        activeSpan.setTag("id.cartao", idCartao);
        activeSpan.setBaggageItem("id.cartao", idCartao);
        activeSpan.log("cadastrando biometria...");

        var novaBiometria = biometriaRequest.toModel(cartao);
        novaBiometria = transacao.salvaEComita(novaBiometria);

        URI location = uriBuilder.path("/cartoes/{id}/biometrias/{id}").build(cartao.getContasIdCartao(), novaBiometria.getId());
        return ResponseEntity.created(location).build();
    }
}
