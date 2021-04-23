package br.com.zupacademy.desafioproposta.cartao.bloqueio;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.Status;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
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

    private final Transacao transacao;

    public BloqueioCartaoController(Transacao transacao) {
        this.transacao = transacao;
    }

    @PutMapping("/{idCartao}/bloqueios")
    public ResponseEntity<?> bloqueia(@PathVariable String idCartao, HttpServletRequest httpRequest) {

        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }
        if (cartao.getStatus() == Status.BLOQUEADO) {
            var error = new APIErrorHandler(List.of(new FieldError("Cartao", "idCartao", "cartão já bloqueado no " +
                    "sistema")), HttpStatus.UNPROCESSABLE_ENTITY);
            return ResponseEntity.unprocessableEntity().body(error);
        }
        if (httpRequest.getHeader("User-Agent") == null) {
            var error = new APIErrorHandler(List.of(new FieldError("Cartao", "User-Agent", "esse campo não deve ser " +
                    "nullo")));
            return ResponseEntity.badRequest().body(error);
        }

        cartao.setStatus(Status.BLOQUEADO);
        cartao.setBloqueio(new Bloqueio(httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"), cartao));

        transacao.atualizaEComita(cartao);

        return ResponseEntity.ok().build();
    }
}
