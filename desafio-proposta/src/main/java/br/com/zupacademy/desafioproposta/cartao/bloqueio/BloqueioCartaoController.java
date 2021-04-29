package br.com.zupacademy.desafioproposta.cartao.bloqueio;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.StatusCartao;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import io.opentracing.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/cartoes")
public class BloqueioCartaoController {

    private final Tracer tracer;
    private final Transacao transacao;

    public BloqueioCartaoController(Tracer tracer, Transacao transacao) {
        this.tracer = tracer;
        this.transacao = transacao;
    }

    @PutMapping("/{idCartao}/bloqueios")
    public ResponseEntity<?> bloqueia(@PathVariable String idCartao, HttpServletRequest httpRequest) {

        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }
        if (cartao.getStatus() == StatusCartao.BLOQUEADO) {
            var error = new APIErrorHandler(List.of(new FieldError("Cartao", "idCartao", "cartão já bloqueado no " +
                    "sistema")), HttpStatus.UNPROCESSABLE_ENTITY);
            return ResponseEntity.unprocessableEntity().body(error);
        }
        if (httpRequest.getHeader("User-Agent") == null) {
            var error = new APIErrorHandler(List.of(new FieldError("Cartao", "User-Agent", "esse campo não deve ser " +
                    "nullo")));
            return ResponseEntity.badRequest().body(error);
        }

        var activeSpan = tracer.activeSpan();
        activeSpan.setTag("id.cartao", idCartao);
        activeSpan.setBaggageItem("user.agent", httpRequest.getHeader("User-Agent"));
        activeSpan.log("bloqueando cartão...");

        cartao.setStatus(StatusCartao.BLOQUEADO);
        cartao.setBloqueio(new Bloqueio(httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"), cartao));

        transacao.atualizaEComita(cartao);

        return ResponseEntity.ok().build();
    }
}
