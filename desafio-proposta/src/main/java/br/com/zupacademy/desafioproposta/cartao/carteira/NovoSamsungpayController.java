package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.EventosCartao;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import io.opentracing.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

import static br.com.zupacademy.desafioproposta.cartao.carteira.NomeCarteira.PAYPAL;
import static br.com.zupacademy.desafioproposta.cartao.carteira.NomeCarteira.SAMSUNG_PAY;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping("/cartoes")
public class NovoSamsungpayController {

    private final Tracer tracer;
    private final Transacao transacao;
    private final EventosCartao eventosCartao;

    public NovoSamsungpayController(Tracer tracer, Transacao transacao, EventosCartao eventosCartao) {
        this.tracer = tracer;
        this.transacao = transacao;
        this.eventosCartao = eventosCartao;
    }

    @PostMapping("/{idCartao}/carteiras/samsungpay")
    public ResponseEntity<?> associa(@PathVariable String idCartao,
                                     @RequestBody @Valid NovaCarteiraRequest carteiraRequest,
                                     BindingResult result, UriComponentsBuilder uriBuilder) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new APIErrorHandler(result.getFieldErrors()));
        }

        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }
        if (cartao.associadoACarteira(SAMSUNG_PAY)) {
            var errors = new APIErrorHandler(List.of(new FieldError("CarteiraDigital", "idCartao",
                    "esse cartão já está associado ao Samsung Pay")), UNPROCESSABLE_ENTITY);
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(errors);
        }

        var activeSpan = tracer.activeSpan();
        activeSpan.setTag("id.cartao", idCartao);
        activeSpan.setBaggageItem("carteira.nome", SAMSUNG_PAY.name());
        activeSpan.log("associando cartão ao Samsung Pay...");

        var novaCarteira = carteiraRequest.toModel(SAMSUNG_PAY, cartao);
        novaCarteira = transacao.salvaEComita(novaCarteira);

        eventosCartao.associaCarteira(idCartao, novaCarteira);

        var location = uriBuilder.path("cartoes/{idCartao}/carteiras/samsungpay/{id}").build(idCartao,
                novaCarteira.getId());
        return ResponseEntity.created(location).build();
    }
}
