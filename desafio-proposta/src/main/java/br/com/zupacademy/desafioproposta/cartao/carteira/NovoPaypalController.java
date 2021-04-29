package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.EventosCartao;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.zupacademy.desafioproposta.cartao.carteira.NomeCarteira.PAYPAL;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping("/cartoes")
public class NovoPaypalController {

    private final Transacao transacao;
    private final EventosCartao eventosCartao;

    public NovoPaypalController(Transacao transacao, EventosCartao eventosCartao) {
        this.transacao = transacao;
        this.eventosCartao = eventosCartao;
    }

    @PostMapping("/{idCartao}/carteiras/paypal")
    public ResponseEntity<?> associa(@PathVariable String idCartao,
                                     @RequestBody @Valid NovaCarteiraRequest carteiraRequest,
                                     BindingResult result, UriComponentsBuilder uriBuilder) { // o BindingResult deve
        // vir logo após o objeto que está sendo validado!
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new APIErrorHandler(result.getFieldErrors()));
        }

        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }
        if (cartao.associadoACarteira(PAYPAL)) {
            var errors = new APIErrorHandler(List.of(new FieldError("CarteiraDigital", "idCartao",
                    "esse cartão já está associado ao Paypal")), UNPROCESSABLE_ENTITY);
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(errors);
        }

        var novaCarteira = carteiraRequest.toModel(PAYPAL, cartao);
        novaCarteira = transacao.salvaEComita(novaCarteira);

        eventosCartao.associaCarteira(idCartao, novaCarteira);

        URI location = uriBuilder.path("cartoes/{idCartao}/carteiras/paypal/{id}").build(idCartao, novaCarteira.getId());
        return ResponseEntity.created(location).build();
    }
}
