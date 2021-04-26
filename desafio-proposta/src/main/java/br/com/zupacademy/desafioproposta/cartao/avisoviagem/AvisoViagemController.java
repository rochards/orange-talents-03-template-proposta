package br.com.zupacademy.desafioproposta.cartao.avisoviagem;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/cartoes")
public class AvisoViagemController {

    private final Transacao transacao;

    public AvisoViagemController(Transacao transacao) {
        this.transacao = transacao;
    }

    @PostMapping("{idCartao}/avisos")
    public ResponseEntity<?> registra(@PathVariable String idCartao, @RequestBody @Valid AvisoViagemRequest avisoRequest,
                                      BindingResult result, HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new APIErrorHandler(result.getFieldErrors()));
        }
        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }

        var novoAviso = avisoRequest.toModel(httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"), cartao);
        transacao.salvaEComita(novoAviso);

        return ResponseEntity.ok().build();
    }
}
